# How to Control SV Server from Command Line

You can deploy a project onto the server, change the mode of a service or list/view services on the server.

To run the SVConfigurator you have to do following:

* Open a command prompt
* Navigate to SVConfigurator directory
* Run the tool by **`SVConfigurator <command> [options]`** at the command line
  * **command** - VSConfigurator executes several commands that will be discussed later
  * **options**  - Options depends on single commands 
* If you want to work with encrypted project (**`-w`** or **`--project-password options`**), you must download and install JCE Unlimited Strength Jurisdiction Policy Files for your JDK version from https://www.oracle.com/technetwork/java/javase/downloads/jce-all-download-5170447.html

In the following text we will discuss single commands that SVConfigurator supports.

## Deploy

The command is used to deploy (or undeploy) the project (or single service from the project) to the Server. You can run it by typing **`SVConfigurator DEPLOY [options] <project file>`** with following options:

| **Option** | **Description** |
| - | - |
| **`<project file>`** | Path to a project file (.vproj or .vproja) whose services will be deployed to the server. |
| **`-w`** or **`--project-password`** | If the project is encrypted, specifies a password to decrypt the project content. |
| **`-f`** or **`--force`** | Force mode. If the service to be deployed is locked, the command will automatically unlock (and lock it by our client) it. Be carefull with this option because you can remove the data of another user. |
| **`-u`** or **`--undeploy`** | Command will undeploy the project (or servcie) from server. |
| **`-s <svc>`** or **`--service <svc>`** | If you specify this option, command will deploy only the specified service. You can specify service by it’s name or by it’s ID. |
| **`-url <url>`** or **`--mgmt-url <url>`** | URL of the server’s management endpoint. |
| **`-usr <user>`** or **`--username <user>`** | Username for server’s management endpoint. |
| **`-pwd <pwd>`** or **`--password <pwd>`** | Password for server’s management endpoint. |

## ChangeMode

This command can be used to change the runtime mode (Learning, Simulating, StandBy) of the service on the server. You can run it by typing **`SVConfigurator CHANGEMODE [options] <service identification> <service mode>`** with following parameters:

| **Option** | **Description** |
| - | - |
| **`<service identification>`** | Identification of the service whose mode will be changed. You can use either name or ID of the service. Note that if there are more than one service with the same name on the server, you have to either specify the project file (where the service is) or identify the service by ID. |
| **`<service mode>`** | The mode we want to switch the service to. It can be one of: `LEARNING`, `SIMULATING`, `STAND_BY` |
| **`-f`** or **`--force`** | Force mode. If the service whose mode we want to change is locked, the command will automatically unlock (and lock it by our client) it. Be carefull with this option because you can remove the data of another user. |
| **`-dm <model>`** or **`--data-model <model>`** | Data model identifiaction (name or ID) to be used. Data model can be (especially have to be) specified only for `LEARNING` and `SIMULAING` modes. |
| **`-pm <model>`** or **`--perf-model <model>`** | Performance model identification (name or ID) to be used. |
| **`-p <source_path>`** or **`--project <source_path>`** | Project file (.vproj or .vproja). You can specify the project file either if you want to use it‘s management URL or if there are more than one service with the same name on the server (if they are not the same, of course) and you want to use service’s name for its identification. |
| **`-w`** or **`--project-password`** | If the project is encrypted, specifies a password to decrypt the project content. |
| **`-url <url>`** or **`--mgmt-url <url>`** | URL of the server’s management endpoint. |
| **`-usr <user>`** or **`--username <user>`** | Username for server’s management endpoint. |
| **`-pwd <pwd>`** or **`--password <pwd>`** | Password for server’s management endpoint. |

## View

This command prints the information about a service on the server. You can run it by typing **`SVConfigurator VIEW [options] <service identification>`** with following parameters:

| **Option** | **Description** |
| - | - |
| **`service identification`** | Identification of the service we want to view. You can use either name or ID of the service. Note that if there are more than one service with the same name on the server, you have to either specify the project file (where the service is) or identify the service by ID. |
| **`-r`** or **`--report`** | If you want to view also the runtime report of the service. |
| **`-p <source_path>`** or **`--project <source_path>`** | Project file (.vproj or .vproja). You can specify the project file either if you want to use it‘s management URL or if there are more than one service with the same name on the server (if they are not the same, of course) and you want to use service’s name for its identification. |
| **`-w`** or **`--project-password`** | If the project is encrypted, specifies a password to decrypt the project content. |
| **`-url <url>`** or **`--mgmt-url <url>`** | URL of the server’s management endpoint. |
| **`-usr <user>`** or **`--username <user>`** | Username for server’s management endpoint. |
| **`-pwd <pwd>`** or **`--password <pwd>`** | Password for server’s management endpoint. |

## List

The command lists the services (and its basic info) deployed onto the server. You can run the command from command line by **`SVConfigurator LIST [options]`** with following parameters:

| **Option** | **Description** |
| - | - |
| **`-p <source_path>`** or **`--project <source_path>`** | Project file (.vproj or .vproja). You can specify the project file either if you want to use it‘s management URL or if there are more than one service with the same name on the server (if they are not the same, of course) and you want to use service’s name for its identification. |
| **`-w`** or **`--project-password`** | If the project is encrypted, specifies a password to decrypt the project content. |
| **`-url <url>`** or **`--mgmt-url <url>`** | URL of the server’s management endpoint. |
| **`-usr <user>`** or **`--username <user>`** | Username for server’s management endpoint. |
| **`-pwd <pwd>`** or **`--password <pwd>`** | Password for server’s management endpoint. |

## Unlock

By this command you can unlock (or lock it again but with your client lock) the service deployed on the server. You can run the command on the command line by typing **`SVConfigurator UNLOCK [options] <service_identification>`** with following parameters:

| **Option** | **Description** |
| - | - |
| **`service identification`** | Identification of the service we want to unlock (or lock again). You can use either name or ID of the service. Note that if there are more than one service with the same name on the server, you have to either specify the project file (where the service is) or identify the service by ID. |
| **`-l`** or **`--lock`** | Locks the service by your client lock right after unlocking |
| **`-url <url>`** or **`--mgmt-url <url>`** | URL of the server’s management endpoint. |
| **`-usr <user>`** or **`--username <user>`** | Username for server’s management endpoint. |
| **`-pwd <pwd>`** or **`--password <pwd>`** | Password for server’s management endpoint. |
