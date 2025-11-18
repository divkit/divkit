#!/usr/bin/env python3
import socket
import json
import uuid
import os
import subprocess
import threading
import time
import signal
import sys
from datetime import datetime
from typing import Optional, Callable, Dict, Any, Union

OUTPUT_PORT = 7969
INPUT_PORT = 7970
LOCALHOST = '127.0.0.1'

class DivJsonEmitterService:
    """Service class for emitting div-json to div client with hot-reload enabled"""

    def __init__(self, json_file: str, port: int) -> None:
        self._json_file: str = json_file
        self._port: int = port
        self._server_socket: Optional[socket.socket] = None
        self._running: bool = False
        self._thread: Optional[threading.Thread] = None

    def start(self) -> None:
        """Start the div-json emitter service in a separate thread"""
        if self._running:
            return

        self._running = True
        self._thread = threading.Thread(target=self._run_server)
        self._thread.daemon = True
        self._thread.start()
        self._log('started on thread')

    def stop(self) -> None:
        """Stop the div-json emitter service"""
        if not self._running:
            return

        self._running = False
        if self._server_socket:
            try:
                self._server_socket.close()
            except:
                pass
        if self._thread and self._thread.is_alive():
            self._thread.join(timeout=2)
            self._log('stopped')

    def _run_server(self) -> None:
        """Main server loop - runs in separate thread"""
        self._server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self._server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)

        try:
            self._server_socket.bind((LOCALHOST, self._port))
            max_connections: int = 3
            self._server_socket.listen(max_connections)
            self._log(f'running on {LOCALHOST}:{self._port}')

            while self._running:
                self._log(f'Waiting for connections...')
                try:
                    client_sock, client_addr = self._server_socket.accept()
                    self._log(f'Client connected: {client_addr}')
                    self._send_div_json_to(client_sock)
                    self._log(f'Closing connection to: {client_addr}')
                    client_sock.close()
                except socket.error as e:
                    if self._running:
                        self._log(f'Client connection error: {e}')
                    break
        except Exception as e:
            self._log(f'Error: {e}')
        finally:
            if self._server_socket:
                self._server_socket.close()

    def _send_div_json_to(self, client_socket: socket.socket) -> None:
        try:
            with open(self._json_file, 'r') as f:
                json_data: str = f.read()

            self._log(f'Sending div-json to client')
            client_socket.sendall(json_data.encode('utf-8'))
            self._log(f'Div-json sent to client')

            # Give the client time to receive the data before closing
            time.sleep(0.1)
        except Exception as e:
            self._log(f'Error sending div json to client: {e}')

    def _log(self, message: str) -> None:
        log_with_timestamp(f'DivJson Emitter: {message}')

class DivJsonReceiverService:
    """Service class for receiving div-json data via socket server"""

    def __init__(self, json_file: str, port: int) -> None:
        self._json_file: str = json_file
        self._port: int = port
        self._server_socket: Optional[socket.socket] = None
        self._running: bool = False
        self._thread: Optional[threading.Thread] = None

    def _handle_json_receiver(self, client_socket: socket.socket) -> None:
        client_addr = client_socket.getpeername()
        self._log(f'Client connected from {client_addr}')

        try:
            data: bytes = b''
            chunk_count: int = 0
            start_time: float = time.time()

            while True:
                chunk: bytes = client_socket.recv(4096)
                if not chunk:
                    break
                data += chunk
                chunk_count += 1
                self._log(f'Received chunk {chunk_count}, size: {len(chunk)} bytes')

            receive_time: float = time.time() - start_time
            json_string: str = data.decode('utf-8')
            self._log(f'Total data received: {len(json_string)} characters in {chunk_count} chunks (took {receive_time:.3f}s)')

            try:
                parsed_json = json.loads(json_string)
                self._log(f'Valid JSON received!')

                with open(self._json_file, 'w') as f:
                    json.dump(parsed_json, f, indent=4)
                self._log(f'Successfully updated {self._json_file} with received json!')

            except json.JSONDecodeError as e:
                self._log(f'Invalid json received: {e}')
        except Exception as e:
            self._log(f'Error handling json receiver: {e}')
        finally:
            self._log(f'Closing connection to {client_addr}')
            client_socket.close()

    def _run_server(self) -> None:
        """Main server loop - runs in separate thread"""
        while self._running:
            self._run_server_loop()
            if self._running: 
                self._log(f'Server loop interrupted will try to restart it in a second')
                time.sleep(1)
    
    def _run_server_loop(self) -> None:
        self._log(f'Starting server on {LOCALHOST}:{self._port}')
        self._server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self._server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)

        try:
            self._server_socket.bind((LOCALHOST, self._port))
            self._server_socket.listen(5)
            self._log(f'Server receiving DivJson on {LOCALHOST}:{self._port}')

            while self._running:
                try:
                    self._log(f'Waiting for connections...')
                    client_sock, client_addr = self._server_socket.accept()
                    client_thread = threading.Thread(target=self._handle_json_receiver,
                                                     args=(client_sock,))
                    client_thread.daemon = True
                    client_thread.start()
                    self._log(f'Started thread!')
                except socket.error as e:
                    if self._running:
                        self._log(f'Socket error: {e}')
                    break
        except Exception as e:
            if self._running:
                self._log(f'Error in server: {e}')
        finally:
            self._log('Closing server socket')
            if self._server_socket:
                self._server_socket.close()

    def start(self) -> None:
        """Start the div-json receiver service in a separate thread"""
        if self._running:
            return

        self._running = True
        self._thread = threading.Thread(target=self._run_server)
        self._thread.daemon = True
        self._thread.start()
        self._log(f'started on thread')

    def stop(self) -> None:
        """Stop the div-json receiver service"""
        if not self._running:
            return

        self._running = False
        if self._server_socket:
            try:
                self._server_socket.close()
            except:
                pass

        if self._thread and self._thread.is_alive():
            self._thread.join(timeout=2)
            self._log('stopped')

    def _log(self, message: str) -> None:
        log_with_timestamp(f'DivJson Receiver: {message}')

    def update_json_file(self, new_json_file: str) -> None:
        """Update the JSON file path"""
        self._json_file = new_json_file
        self._log(f'updated to receive JSON for: {self._json_file}')


def log_with_timestamp(message: str) -> None:
    timestamp = datetime.now().strftime('%H:%M:%S.%f')[:-3]  # Include milliseconds
    print(f'[{timestamp}] {message}', flush=True)

def try_open_file_with_default_app(file_path: str) -> None:
    """Try to open a file with the default application for the current platform"""
    try:
        if sys.platform == 'darwin':  # macOS
            subprocess.Popen(['open', file_path])
        else:  # Linux/Unix
            subprocess.Popen(['xdg-open', file_path])
        log_with_timestamp(f'Opened file with default application: {file_path}')
    except FileNotFoundError:
        log_with_timestamp(f'Could not open file (file opener not available in this environment): {file_path}')
    except Exception as e:
        log_with_timestamp(f'Could not open file: {e}')

def create_json_file() -> str:
    filename: str = f'/tmp/{uuid.uuid4()}.json'

    json_content: Dict[str, Any] = {
        'log_id': 'hot_reload_template',
        'states': [
            {
                'state_id': 0,
                'div': {
                    'type': 'container',
                    'margins': {'top': 8, 'bottom': 8, 'left': 8, 'right': 8},
                    'items': [
                        {
                            'type': 'container',
                            'items': [
                                {
                                    'type': 'text',
                                    'text': 'Hot Reload Enabled',
                                    'font_size': 21,
                                    'font_weight': 'bold',
                                    'margins': {'bottom': 8}
                                },
                                {
                                    'type': 'text',
                                    'text': f'Hello this is "Hot Reload"! We are waiting for card to send us its div-json so you could edit it.\nCurrently rendered div-json is located at server: \n{filename}',
                                    'font_size': 14,
                                    'margins': {'bottom': 8}
                                },
                                {
                                    'type': 'text',
                                    'text': 'Hot reload documentation',
                                    'font_size': 16,
                                    'text_color': '#00f',
                                    'underline': 'single',
                                    'margins': {'bottom': 8},
                                    'action': {
                                        'url': 'https://divkit.tech/docs/ru/quickstart/android-faq#hot-reload',
                                        'log_id': 'docs'
                                    }
                                }
                            ],
                            'orientation': 'vertical',
                            'border': {
                                'corner_radius': 16,
                                'stroke': {'color': '#aaa', 'width': 1}
                            },
                            'paddings': {'top': 12, 'bottom': 12, 'left': 12, 'right': 12}
                        }
                    ]
                }
            }
        ]
    }

    with open(filename, 'w') as f:
        json.dump(json_content, f, indent=4)
    return filename

def main() -> None:
    # Check if a json file is specified as the first argument
    sync_with_client: bool = True
    if len(sys.argv) > 1:
        target_json: str = sys.argv[1]
        if os.path.exists(target_json):
            json_file: str = target_json
            sync_with_client = False
            log_with_timestamp(f'Using json file: {json_file} (Incoming json updates will be declined)')
        else:
            log_with_timestamp(f'Error: File "{target_json}" does not exist')
            sys.exit(1)
    else:
        json_file: str = create_json_file()
        log_with_timestamp(f'Will serve: {json_file}')

    # Try to open the JSON file with the default application
    try_open_file_with_default_app(json_file)

    json_emitter = DivJsonEmitterService(json_file, OUTPUT_PORT)
    json_emitter.start()
    json_receiver = None
    if sync_with_client:
        json_receiver = DivJsonReceiverService(json_file, INPUT_PORT)
        json_receiver.start()



    def signal_handler(sig: int, frame: Any) -> None:
        log_with_timestamp(f'Received signal {sig}, shutting down...')
        json_emitter.stop()
        if json_receiver:
            json_receiver.stop()

    signal.signal(signal.SIGINT, signal_handler)
    signal.signal(signal.SIGTERM, signal_handler)

    # Simple main loop without try-except
    while json_emitter._running:
        time.sleep(1)
    
    log_with_timestamp('Json emitter stopped, shutting down...')
    if json_receiver:
        json_receiver.stop()
        log_with_timestamp('Json receiver stopped.')

    log_with_timestamp('Server is stopped.')  

if __name__ == '__main__':
    main()
