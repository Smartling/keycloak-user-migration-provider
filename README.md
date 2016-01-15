# Keycloak Migration User Federation Provider
  
Provides a [Keycloak][0] [user federation provider][1] for migrating users
from a legacy system to Keycloak with zero downtime. For more information,
read our post, [Migrate to Keycloak with Zero Downtime][3] on the Smartling Engineering Blog.

## Key features of the migration provider

1. Replace your legacy login system with Keycloak without downtime
2. Migrate users to Keycloak without having to reset their passwords
3. If something totally unexpected goes wrong with your new system launch, you can
roll back with little to no user facing impact

## What's Provided

1. The user-migration-federation-provider
2. A sample legacy-user-app
3. A portal-demo service set up as a Keycloak client 

## How it Works

The Smartling user federation provider will import your users on demand into Keycloak's
local storage, including username and password validation with the legacy system.
For an in depth look at how on demand user migration works, read our post,
[Migrate to Keycloak with Zero Downtime][3] on the Smartling Engineering Blog.

## Installing the Federation Provider

### Install Keycloak

Download [Keycloak 1.7.0.Final][2] and unzip / un-tar it. 

### Clone this Repository

Clone this repository to your local workstation. You will have to edit `gradle.properties`
to point to the location where you installed Keycloak 1.7.0.Final. This location should
not contain spaces in the path.
 
### Install the Federation Provider

To install the Federation and configure Keycloak: 

From this repository, run:

``` bash
./gradlew deployModules
cd <path to keycloak>
```

Open `standalone/configuration/keycloak-server.json` with your preferred text editor.
Update the `"providers"` configuration to include `module:net.smartling.provider.federation`:

``` json
"providers": [
  "classpath:${jboss.server.config.dir}/providers/*",
  "module:net.smartling.provider.federation"
],
```

The federation provider can now be used from Keycloak.

## Running the Provided Examples

This project contains two sample applications as noted above. The first behaves as a legacy
user service maintaining user information for the sample company. The second is a portal
service that simply displays information about the logged in user.
 
The steps below outline how use the examples.

### Start Keycloak & Import the Example Realm

Start Keycloak, if it isn't already running, and log into the master realm as `admin`.
Import the included `demo-realm.json` file.
 
### Start the Legacy User Application

From the location you cloned this repository into, start the legacy user service.

``` bash
$ ./gradlew :legacy-user-app:bootRun
```

### Start the Portal Application

From the location you cloned this repository into, start the demo portal.

``` bash
$ ./gradlew :portal-demo:bootRun
```

### Access the Portal

Open http://localhost:9082 in your web browser. You can log in as any of the sample users
in the legacy user service.

The included sample users are:

* craig@007.com
* green@007.com
* mendes@007.com

For simplicity, all of the user's passwords are `Martini4`.

[0]: http://keycloak.jboss.org/
[1]: http://keycloak.github.io/docs/userguide/keycloak-server/html/user_federation.html
[2]: http://downloads.jboss.org/keycloak/1.7.0.Final/keycloak-1.7.0.Final.tar.gz
[3]: http://tech.smartling.com/migrate-to-keycloak-with-zero-downtime/
