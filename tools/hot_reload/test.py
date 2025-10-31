#!/usr/bin/env python3
import socket
import json
import threading
import time
import subprocess
import os
import sys
import tempfile
import unittest
from unittest.mock import patch
import signal
from typing import Dict, Any, Optional, Union, List


class TestHotReload(unittest.TestCase):
    """Test cases for the hot reload server script"""

    def setUp(self) -> None:
        self.hotReloadServerProcess: Optional[subprocess.Popen] = None
        self.hotReloadClient = HotReloadServerClient()
        self.stdout_file_path: Optional[str] = None
        self.stderr_file_path: Optional[str] = None

    def tearDown(self) -> None:
        """Clean up after each test"""
        print(f"Cleaning up after: {self._testMethodName}")
        try:
            self.releaseHotReloadServerProcess()
            print(f"Waiting server process to disconnect!")
            self.hotReloadClient.waitDisconnected()
        except Exception as e:
            print(f"Error releasing hot reload server process: '{e}'! Other tests may fail!")

        self.printHotReloadServerProcessOutput()

    def printHotReloadServerProcessOutput(self) -> None:
        print(f"Printing server process output for test: {self._testMethodName}")
        stdout_content = ""
        stderr_content = ""

        if self.stdout_file_path and os.path.exists(self.stdout_file_path):
            with open(self.stdout_file_path, 'r') as f:
                stdout_content = f.read()

        if self.stderr_file_path and os.path.exists(self.stderr_file_path):
            with open(self.stderr_file_path, 'r') as f:
                stderr_content = f.read()

        print(f"\n=== SERVER STDOUT ===")
        if stdout_content:
            print(stdout_content)
        else:
            print("(empty)")
        print(f"=== END SERVER STDOUT ===\n")

        print(f"\n=== SERVER STDERR ===")
        if stderr_content:
            print(stderr_content)
        else:
            print("(empty)")
        print(f"=== END SERVER STDERR ===\n")

    def releaseHotReloadServerProcess(self) -> None:
        if self.hotReloadServerProcess:
            print(f"Killing server process!")
            # Send signal to the entire process group
            os.killpg(os.getpgid(self.hotReloadServerProcess.pid), signal.SIGINT)
            self.hotReloadServerProcess.send_signal(signal.SIGINT)
            try:
                self.hotReloadServerProcess.wait(timeout=5)
            except subprocess.TimeoutExpired:
                print(f"Server process timed out! Killing it!")
            finally:
                self.hotReloadServerProcess.kill()
        else:
            print(f"No server process was spawned for test: {self._testMethodName}")

    def startHotReloadScript(self, targetJson: str = '') -> None:
        """Start the hot reload script as a subprocess with optional arguments"""
        scriptPath: str = os.path.join(os.path.dirname(__file__), 'run.sh')

        # Create temporary files for stdout and stderr
        stdout_file = tempfile.NamedTemporaryFile(mode='w', delete=False, suffix='.out')
        stderr_file = tempfile.NamedTemporaryFile(mode='w', delete=False, suffix='.err')
        stdout_file.close()
        stderr_file.close()

        # Store file paths for cleanup
        self.stdout_file_path = stdout_file.name
        self.stderr_file_path = stderr_file.name

        cmd: str = f'{scriptPath} {targetJson} > {stdout_file.name} 2> {stderr_file.name}'

        if targetJson != '' and not os.path.exists(targetJson):
            self.fail(f"Target JSON file does not exist: {targetJson}")

        print(f"Starting hot reload server with command: {cmd}")

        self.hotReloadServerProcess = subprocess.Popen(
            cmd,
            shell=True,
            text=True,
            bufsize=0,
            preexec_fn=os.setsid  # Start a new session/process group
        )

        # Wait for server to start up
        time.sleep(2)

        print("Hot reload server started successfully")

    def createTestJson(self) -> Dict[str, Any]:
        return {
            'log_id': f'Test "{self._testMethodName}". Timestamp: {time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())}'
        }

    def testReceiveJsonFromServer(self) -> None:
        """
        Test Case: Start the script and receive JSON from it by socket.
        """
        print("\n=== Running Test Case: Receive JSON from Server ===")
        self.startHotReloadScript()

        jsonString: str = self.hotReloadClient.receiveDivJson()
        print(f"Received JSON length: {len(jsonString)} characters")

        parsedJson = json.loads(jsonString)
        print("Successfully parsed JSON from server")

        self.assertIn('log_id', parsedJson, f"JSON should contain 'log_id' key! Instead got: {parsedJson}")
        self.assertIn('states', parsedJson, f"JSON should contain 'states' key! Instead got: {parsedJson}")
        print("✓ Test PASSED: Successfully received and validated JSON from server")

    def testSendJsonToServer(self) -> None:
        """
        Test Case: Start the script, send own JSON and check it is received from server by socket.
        """
        print("\n=== Running Test Case: Send JSON to Server ===")
        self.startHotReloadScript()
        testJsonToSend = self.createTestJson()
        self.hotReloadClient.sendDivJson(json.dumps(testJsonToSend))
        print("JSON data sent to server")

        # Wait for server to save json file.
        time.sleep(1)

        receivedJsonString: str = self.hotReloadClient.receiveDivJson()
        receivedJson = json.loads(receivedJsonString)
        print("Successfully parsed updated JSON from server")

        # Verify our sent data is in the received JSON
        self.assertEqual(
            testJsonToSend,
            receivedJson,
            "Server should have updated JSON with our sent data"
        )
        print("✓ Test PASSED: Successfully sent JSON to server and verified it was updated")

    def testTargetedJsonFile(self) -> None:
        """
        Test Case: When the script is started with a targeted JSON file,
        it serves the targeted JSON and declines incoming updates.
        """
        print("\n=== Running Test Case: Targeted JSON file ===")

        tempJsonFile = tempfile.NamedTemporaryFile(mode='w', suffix='.json', delete=False)
        testJsonContent = self.createTestJson()
        json.dump(testJsonContent, tempJsonFile, indent=4)
        tempJsonFile.close()

        # Start the hot reload script that serves the targeted JSON.
        self.startHotReloadScript(tempJsonFile.name)

        receivedJsonString: str = self.hotReloadClient.receiveDivJson()
        receivedJson = json.loads(receivedJsonString)
        self.assertEqual(receivedJson, testJsonContent)
        print("✓ Targeted JSON served correctly")

class HotReloadServerClient:
    """Service class for handling socket operations in tests"""

    def __init__(self) -> None:
        self._host: str = '127.0.0.1'
        self._timeout: int = 10
        self._receivePort: int = 7969
        self._sendPort: int = 7970

    def receiveDivJson(self) -> str:
        try:
            clientSocket: socket.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            clientSocket.settimeout(self._timeout)
            clientSocket.connect((self._host, self._receivePort))

            receivedData: bytes = b""
            while True:
                try:
                    chunk: bytes = clientSocket.recv(4096)
                    if not chunk:
                        break
                    receivedData += chunk
                except socket.timeout:
                    break

            clientSocket.close()

            # Convert bytes to string and verify it's non-empty
            jsonString: str = receivedData.decode('utf-8')
            if not jsonString.strip():
                raise ValueError("Received empty string from server")

            return jsonString

        except socket.error as e:
            raise ConnectionError(f"Failed to connect to {self._host}:{self._receivePort}: {e}")

    def sendDivJson(self, data: Union[str, bytes]) -> None:
        try:
            clientSocket: socket.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            clientSocket.settimeout(self._timeout)
            clientSocket.connect((self._host, self._sendPort))

            clientSocket.sendall(data.encode('utf-8') if isinstance(data, str) else data)
            clientSocket.close()
        except socket.error as e:
            raise ConnectionError(f"Failed to connect to {self._host}:{self._sendPort}: {e}")

    def waitDisconnected(self) -> None:
        for _ in range(5):
            try:
                print("Waiting for disconnected...")
                json = self.receiveDivJson()
                print(f"Still receiving div json! First 100 characters: '{json[:100]}'")
                time.sleep(1)
            except ValueError as e:
                print(f"Still receiving unexpected json! {e}")
            except ConnectionError:
                print("Connection looks closed!")
                return
        raise ValueError("Server is still running!")


def runTests() -> bool:
    """Run all tests with verbose output"""
    print("Starting Hot Reload Tests")
    print("=" * 50)

    suite: unittest.TestSuite = unittest.TestLoader().loadTestsFromTestCase(TestHotReload)
    runner: unittest.TextTestRunner = unittest.TextTestRunner(verbosity=2)
    result: unittest.TestResult = runner.run(suite)

    print("\n" + "=" * 50)
    if result.wasSuccessful():
        print("✓ All tests PASSED!")
    else:
        print("✗ Some tests FAILED!")
        print(f"Failures: {len(result.failures)}")
        print(f"Errors: {len(result.errors)}")

    return result.wasSuccessful()


if __name__ == "__main__":
    success: bool = runTests()
    sys.exit(0 if success else 1)
