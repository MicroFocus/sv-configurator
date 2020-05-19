# SVConfigurator - Micro Focus Service Virtualization configuration and management tool

This tool can deploy a project onto the Service Virtualization Server, change the mode of a service or list/view services on the server.

Micro Focus Service Virtualization:
  * [Service Virtualization overview](https://www.microfocus.com/sv)
  * [Service Virtualization documentation](https://admhelp.microfocus.com/sv/en/)
  * [Micro Focus Marketplace](https://marketplace.microfocus.com/appdelivery/content/service-virtualization)

SVConfigurator supports the following operations:

| **Command** | **Description** |
| - | - |
| **`CHANGEMODE`**      | Changes the runtime mode of the service |
| **`DEPLOYPROJECT`**   | Deploys a project onto the server |
| **`EXPORT`**          | Export projects or a selected service from the server |
| **`HOTSWAP`**         | Performance Model Hot-Swap |
| **`LIST`**            | Prints the list of deployed services on the server |
| **`LISTPROJECT`**     | List the project archive content |
| **`SETLOGGING`**      | Enables or disables message logging for specified service |
| **`UNDEPLOY`**        | Undeploy a project/service from the server |
| **`UNLOCK`**          | Unlocks the service on the server |
| **`UPDATE`**          | Updates a data model and/or a performance model of a selected service from the server (downloads learned data) |
| **`VIEW`**            | Prints the info about a service on the server |


**Note:** If you use encrypted SV project (**`-w`** or **`--project-password options`**), you must download and install JCE Unlimited Strength Jurisdiction Policy Files for your JDK version from https://www.oracle.com/technetwork/java/javase/downloads/jce-all-download-5170447.html
