[volver](./README.md)
# Usage
Once the server application is installed, a user creation page will be displayed when you access the main page:
![container_use_3](./server_images/server_image_3.png)
This page will only be shown if the application detects that there are no accounts stored in the database, and it will only be accessible under these circumstances. Once the data is entered, the user will be redirected to the login page.
![server_1](./server_images/server_image_4.png)
<br><br>
After logging in, the main page of the application will be shown. This page displays a series of elements that vary depending on the permissions of the user who has logged in.
![server_2](./server_images/server_image_5.png)

## Main page
From this page, we will have access to the navbar, which contains links to all pages of the application, including the account management pages, the machine management page, the log management page, and the user account options.
In addition to the navbar, as seen in the previous image, there is an element that shows the machines that require review, meaning those associated with a log type that is classified as needing review.
![server_3](./server_images/server_image_6.png)
The card displayed is purely informational and shows the name and IP of the machine that requires review. However, to access the log or machine information, it is necessary to navigate to the relevant page.

## User management pages
By clicking on the Accounts option in the navigation menu, three options will be displayed: Users, Roles, and Permissions.
![server_4](./server_images/server_image_7.png)
The first option redirects to the user management page, where, in addition to being able to access the other two pages, the user can create, edit, disable, and enable accounts.
![server5](./server_images/server_image_8.png)
If an account is disabled, it cannot be used, so care should be taken not to disable the administrator account to avoid problems.
When clicking the New User button, a form will open where the credentials for the user to be created must be entered:
![server_6](./server_images/server_image_9.png)
![server_7](./server_images/server_image_10.png)
An important detail is that a user can be created without roles, but if no role is assigned, they will only be able to access the home page.<br>
When editing a user, for security reasons, it will not be allowed to change that user's password.
<br><br>
By accessing the roles page, all created roles will be shown. By default, the application only creates the ADMIN role, which has all permissions.
![server_8](./server_images/server_image_11.png)
From this page, it is also possible to create and edit roles. When creating or editing a role, a form will be displayed showing the permissions stored in the database and a field to name the role.
![serve_9](./server_images/server_image_12.png)
![serve_10](./server_images/server_image_13.png)
Of course, this new role can be assigned to a user, granting them the corresponding permissions:
![serve_11](./server_images/server_image_14.png)
![serve_12](./server_images/server_image_15.png)
<br><br>
The last page related to user account management is the permissions page, which shows all permissions offered by the application.
![serve_13](./server_images/server_image_16.png)
These permissions are created when the database is initialized with the relevant script, and because of the way the application is structured, it is not possible to add or change their behavior without modifying the code.
Below is a description of the actions allowed by the permissions:
<table>
    <tr>
        <th>MACHINE_MANAGEMENT</th>
        <td>Allows access to pages and requests to the endpoints dedicated to displaying and modifying machines</td>
    </tr>
        <tr>
        <th>MANAGE_ROLES</th>
        <td>Allows access to the pages and endpoints that permit creating, viewing, and modifying roles</td>
    </tr>
        <tr>
        <th>SOLVE_LOGS</th>
        <td>Allows access to the pages and endpoints dedicated to marking records as resolved</td>
    </tr>
        <tr>
        <th>USER_MANAGEMENT</th>
        <td>Allows access to the pages and endpoints dedicated to creating and modifying users</td>
    </tr>
        <tr>
        <th>VIEW_LOGS</th>
        <td>Allows access to the pages and endpoints dedicated to showing logs and their details</td>
    </tr>
</table>

## Machine-related pages
By accessing the Machines page from the navigation menu, a grid with all machines stored in the database will be displayed. In addition, depending on the machine state, the card will change color: white if it does not require review, yellow if it requires review, and red if it requires urgent review. A label will also indicate whether it is connected or not.
![server_14](./server_images/server_image_17.png)
In addition to the machines, there is also a filter form that can be used to find a specific machine when needed. The form allows:
- Search by name
- Search by IP
- Show disabled machines
- Show only connected machines
- Show only machines that require review

![server_15](./server_images/server_image_18.png)
<br><br>

By clicking on one of the cards, the machine information will be displayed. On this page, it is possible to see the frequency of regular logs, the registration date, description, name, IP, and the machine logs divided into two tabs. In addition, a form is shown that allows changing the machine configuration if it is connected.
![server_16](./server_images/server_image_19.png)
![server_17](./server_images/server_image_20.png)

Next to the description and name fields, edit buttons are shown, allowing the user to enter an editing mode to modify these fields.
If you want to view the details of one of the machine's logs, click the relevant button on the record you want to inspect. Doing so will display a page with the log information and, if possible, a button to mark the log as reviewed.
![server_18](./server_images/server_image_21.png)
After marking a record as reviewed, its color changes to green, and the machine icon color adjusts to match the highest-priority unresolved log.
![server20](./server_images/server_image_22.png)
![server19](./server_images/server_image_23.png)

## Log-related pages
The third option in the navigation bar allows access to a list of all logs requiring review from all machines stored in the database.
![server21](./server_images/server_image_24.png)

From this page, it is possible to view the details of a log, the machine from which it came, or validate multiple logs at once by selecting them individually or choosing the option to select all and then clicking the "mark as reviewed" button.
![server22](./server_images/server_image_25.png)
![server23](./server_images/server_image_26.png)

## Account settings pages
The pages related to user account actions can be accessed from the user icon in the navigation bar.
![server24](./server_images/server_image_27.png)
The first option redirects to a form where the password can be changed after entering the current one.
![server25](./server_images/server_image_28.png)
![server26](./server_images/server_image_29.png)
When the password is changed, all sessions assigned to the user (refresh tokens) are marked invalid, so when the user's access token expires, they will be asked to log in again.

The second option logs the user out by "removing" the cookies with the user tokens.

## Connection with client machines
When the server starts, an additional thread is also started to configure a server socket that accepts any machine attempting to connect to the server, registering it if it is not already registered. The only way to prevent a connection from a client machine is to block it through the interface itself. This is, of course, a serious vulnerability in the application. However, due to different problems encountered during development and a lack of time, the necessary security could not be implemented for this connection. In the future, it is planned to implement an authentication system based on mTLS certificates, which would also allow connections from machines outside the network.