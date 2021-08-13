## MTD (Medical Term Dissector)
 This app is intended to teach people how to define common medical terms without the use of a dictionary. 
 It does this based on training the user to understand common roots prefixes and suffixes found in the majority of medical terms.

For example:
hypoglycemia 
* hypo = below normal or lacking
* glyc = sugar
* emia = blood 

So based on this, you can understand that hypoglycemia means "lacking sugar in the blood".

### Design Principles
* An inversion of control pattern mostly done with dependency injection. (since inversion of control is central to Spring Framework design)
* RESTful api.
* MVC (Model View Controller) design pattern.
* Reactive Redis and Reactive Couchbase to handle json documents both in repository and in database.
* Mariadb for relational database.
* rxjava3 to run as many things reactively as possible.
* email client and OAuth2 to send emails for login reset.


### Setup
First, run the setup.sql script in mariadb container by either copying the contents to mysql client, or by linking file
to the location of scripts folder for container.