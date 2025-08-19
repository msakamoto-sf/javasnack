# ConfusingCertificate : MITM Proxy の証明書生成で混乱を招きそうな証明書

QUESTION: Where Common Name come from when Burp generates MITM server certificate ?

work directory:

```
$ cd javasnack/src/main/resources/jsse/ConfusingCertificate/
```

## generate and prepare pkcs12 keystore

1. generate rsa 2048 private key
    ```
    $ openssl genpkey -algorithm RSA -out server.key -pkeyopt rsa_keygen_bits:2048
    ```
2. prepare "openssl req -new -x509" configuration file with subjectAltName extension
    ```
    $ cat <<EOF > req-x509-with-san.conf
    [req]
    default_bits       = 2048
    prompt             = no
    default_md         = sha256
    distinguished_name = my_dn
    req_extensions     = my_req_ext

    [my_dn]
    CN = dn-cn.test

    [my_req_ext]
    subjectAltName = @my_alt_names

    [my_alt_names]
    DNS.1 = *.test
    DNS.2 = san-1.test
    DNS.3 = san-2.test
    EOF
    ```
3. generate self-signed certificate (valid for 10 years)
    ```
    $ openssl req -new -x509 -key server.key -out server.crt -days 3650 -config req-x509-with-san.conf -extensions my_req_ext

    $ openssl x509 -in server.crt -text -noout
    Certificate:
        Data:
            Version: 3 (0x2)
    (...)
            Issuer: CN=dn-cn.test
    (...)
            X509v3 extensions:
                X509v3 Subject Alternative Name:
                    DNS:*.test, DNS:san-1.test, DNS:san-2.test
    ```
4. create pkcs12 keystore (you will be prompted for an export password)
    ```
    $ openssl pkcs12 -export -out server.p12 -inkey server.key -in server.crt -name "ConfusingCertificate"
    Enter Export Password:(Enter)

    Verifying - Enter Export Password:(Enter)
    ```

## demo

1. start Burp Proxy at `localhost:8080`
2. run ConfusingCertificateWebServer at `localhost:8081`
    ```
    $ java -jar (javasnack.jar) ConfusingCertificateWebServer 8081
    ```
3. run ConfusingCertificateProxiedClient
    ```
    $ java -jar (javasnack.jar) ConfusingCertificateProxiedClient localhost 8080 8081
    cert[0] - Subject: CN=localhost,OU=PortSwigger CA,O=PortSwigger,C=PortSwigger
    cert[0] - Subject Alternative Name[0] is DNS value of: localhost
    cert[1] - Subject: CN=PortSwigger CA,OU=PortSwigger CA,O=PortSwigger,L=PortSwigger,ST=PortSwigger,C=PortSwigger
    cert[1] - not contains Subject Alternative Names
    HTTP/1.1 200 OK
    Content-Type: text/plain

    Hello from ConfusingCertificateWebServer!
    ```

CONCLUSION: Burp generate MITM server certificate which CN is taken from `CONNECT` method request line.
SAN also comes from same source.
