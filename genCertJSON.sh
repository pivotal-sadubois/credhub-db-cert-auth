#!/bin/bash

pCert=$(base64 ~/.postgresql/postgresql.crt.der) 
pKey=$(base64 ~/.postgresql/postgresql.key.pk8)
pRoot=$(base64 ~/.postgresql/root.crt.der)

echo -e "{\n  \"rootCert\": \"$pRoot\",\n  \"sslCert\": \"$pCert\",\n   \"sskKey\": \"$pKey\"\n}" > creds.json 
