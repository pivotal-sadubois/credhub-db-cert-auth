# DB Example

This project uses client certificates to authenticate to PostgreSQL. Sample endpoint for testing is `/bookmarks`, actuator exposes all. Do not use in production. 

## Prepare PostgreSQL certificates

We need server certificate and client certificate for authentication. PostgreSQL driver wants the client certificates as der (ssl) and pk8 (key) files.

### 1. Convert .crt and .key files to supported formats

Server certificate as .crt is accepted.

Convert client SSL cert to .der:

`openssl x509 -in postgresql.crt -out postgresql.crt.der -outform der`

Convert client cert key to .pk8:

`openssl pkcs8 -topk8 -outform DER -in postgresql.key -out postgresql.key.pk8 -nocrypt`

### 2. Create JSON file for CredHub

To create the CredHub service (CredHub has to be available in Marketplace), prepare the certificates JSON. See file 'creds.json' in project root as example.

```
{
   "rootCert": "<base64 encoded root.crt>",
   "sslCert": "<base64 encoded postgresql.crt.der>",
   "sskKey": "<base64 encoded postgresql.key.pk8>"
 }
```

## Credhub Service

Create the service:

`cf create-service credhub default credhub-cert -c creds.json`

## Create the app

`mvn clean install`

`cf push` (see manifest.yml for details)