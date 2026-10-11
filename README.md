[![CC BY 4.0][cc-by-shield]][cc-by]
# CommanderUSB
> Remote monitoring and control of USB flash drives
This work is licensed under a
[Creative Commons Attribution 4.0 International License][cc-by].

[![CC BY 4.0][cc-by-image]][cc-by]

[cc-by]: http://creativecommons.org/licenses/by/4.0/
[cc-by-image]: https://i.creativecommons.org/l/by/4.0/88x31.png
[cc-by-shield]: https://img.shields.io/badge/License-CC%20BY%204.0-lightgrey.svg

## What it is
This project consists of a pair of applications that allow users to observe and control access to USB flash drives on a specific machine or machines in a network through a control panel from a remote computer.

The applications that make this project work are the client and server applications.

### Client
```
At this moment, the client application is only compatible with Windows 10/11 systems.
```
The client application needs to be installed through the dedicated installer, and it will run in the background, sending information to the server application. The information sent includes:
- Date of the report
- Whether USB flash drives are allowed on the machine
- Whether the machine can mount USB flash drives automatically
- A list of the USB flash drives connected to the machine, if any

The client application will also interpret the information sent by the server application, enabling or disabling USB flash drive access on the machine and changing configuration parameters. It also includes security measures so that if a parameter is changed without authorization, it will block USB access, unmount all USB flash drives currently mounted, and send information to the server application about the modification and the machine status.
Once installed, the client application will launch automatically when the machine starts.

### Server
The server application is a web application that must be installed on a machine connected to the same network as the client machines. It receives and stores the information sent by the client applications in a MariaDB database that must have been created beforehand.
The server application also acts as a control panel for the machines with the client app installed. It includes an account and role system so that the control panel is accessible from a remote device only by users previously registered by an administrator account.

The server application will also allow users to:
- View the information stored in the database
- Open USB access on a machine for a limited time
- Close USB access on a machine
- View suspicious activity and identify which machine it came from

```
At this moment, the only way to connect a client to the server from an outside network would be through a VPN, but this has not been tested and could cause errors. This is because the IP used by the socket connection is used as the client identifier. This will be changed in a future release.
```

# Purpose
This project was created to offer a free and secure surveillance and security system that is relatively easy to install for those who cannot implement a similar solution, since there is no free open-source application that offers a centralized way to monitor and control the use of USB drives.
This application is designed to help those who need a way to prevent data leaks or situations like "it works on my machine, so let me copy the files and paste them onto yours," because in many cases this can create far more problems in the long run than spending a few minutes to understand and fix the real issue.

# Planned features
- mTLS encrypted communication
- Make the client application's presence more noticeable to the user (taskbar icons and warning/alert dialogs)
- Mail system for suspicious activity detection
- Rework and improve the account role system
- Default front-end migration to Angular
- Client app optimization
- Cross-platform support

Although I want this project to be a functional, secure, and valid application on its own, it is still open source, so if you feel capable, please feel free to use it as a base to create a version that better fits your needs.
<br><br>
- [Server app installation](./server_app_installation.md)
- [How to use the server app](./server_app_usage.md)
<br>

- [Client app installation](./client_app_installation.md)
- [Client app usage](./client_app_usage.md)

