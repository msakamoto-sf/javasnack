# ConfusingCertificate : MITM Proxy の証明書生成で混乱を招きそうな証明書

作業ディレクトリ:

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

## run ConfusingCertificateWebServer

```
java -jar (javasnack.jar) ConfusingCertificateWebServer 8081
or
java -jar (javasnack.jar) javasnack.Main ConfusingCertificateWebServer 8081
```

