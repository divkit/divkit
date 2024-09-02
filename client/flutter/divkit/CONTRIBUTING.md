# Notice to external contributors
The flutter client is part of the DivKit project. First of all check out [the general rules for the monorepository](https://github.com/divkit/divkit/blob/main/CONTRIBUTING.md)

In short, when you submit your pull request, please add the following information into it:
```
I hereby agree to the terms of the CLA available at: [link].
```

Replace the bracketed text as follows: [link] is the link to the current version of the CLA: https://yandex.ru/legal/cla/, https://yandex.ru/legal/cla/?lang=en (in English) or https://yandex.ru/legal/cla/?lang=ru (in Russian).
It is enough to provide us such notification once.

# Contribution Guide
Thank you for considering contributing to this project! We welcome contributions in many forms, including bug reports, feature requests, code improvements, and documentation.

## How to Contribute
### Reporting Bugs
If you encounter any bugs, please let us know by following these steps:
1. **Search for Existing Issues**: Before submitting a new bug report, please check if the bug is already reported in [the issue tracker](https://github.com/divkit/divkit/issues).
2. **Create a New Issue**: If no existing issue matches the bug, create a new issue with a descriptive title and detailed information about the bug:
    - Steps to reproduce
    - Expected behavior
    - Actual behavior
    - Screenshots or logs (if applicable)
    - Environment details (e.g., OS, browser, version)

###  Suggesting new or other clients Features
We are always open to new ideas and feature requests. We can also prioritize the implementation of features of other specifications that have not yet been implemented. To suggest a feature, follow these steps:
1. **Search for Existing Issues**: Check if someone has already proposed the feature in [the issue tracker](https://github.com/divkit/divkit/issues).
2. **Create a New Feature Request**: If the feature is not already suggested, create a new issue with a clear description of the feature:
    - Purpose and motivation for the feature
    - Any relevant examples or use cases
    - Potential impact on existing functionality

### Coding Contributions
If you want to contribute code to the project:
1. **Fork the Repository**: Start by forking the repository on GitHub.
2. **Clone Your Fork**: Clone the forked repository to your local machine.
   git clone https://github.com/YOUR_USERNAME/divkit.git
   cd REPO_NAME
3. **Create a Branch**: Create a new branch for your changes.
   git checkout -b my-feature-branch
4. **Make Changes**: Implement your changes, adding comments and documentation as necessary.
   It is important to indicate the changes in the [CHANGELOG](https://github.com/divkit/divkit/blob/main/client/flutter/divkit/CHANGELOG.md).
5. **Run Tests**: Ensure all tests pass and write new tests if you introduced new features.
   From `clients/flutter/divkit` run scripts:
   ```shell
    ./tool/get_test_data.sh
    ./tool/validate.sh
    ./tool/run_unit_tests.sh
    ./tool/run_integration_tests.sh
    ./tool/run_goldens.sh
   ```
6. **Commit Changes**: Commit your changes with a clear and descriptive commit message.
    git commit -m "FLUTTER: Description of the changes"
7. **Push Changes**: Push the changes to your forked repository.
    git push origin my-feature-branch
8. **Create a Pull Request**: Open a pull request from your branch on GitHub. Provide a detailed description of your changes and link any related issues.

### Code Style
The minimum requirements for the code style are controlled by the linter, please write clearly and accessible to others.

### Reviewing Process
Your pull request will be reviewed by one or more maintainers. You might be asked to provide additional information or make changes. Please be patient and responsive to feedback.

### Contact Us
Telegram chat: (in English) [@divkit_community_en](https://t.me/divkit_community_en)
               (in Russian) [@divkit_community_ru](https://t.me/divkit_community_ru)