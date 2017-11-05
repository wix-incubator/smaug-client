# Smaug Client
Wix's Smaug exchanges full credit card information (number, expiry date, CSC, etc) with an opaque token, accompanied by non-sensitive card information (last 4 digits of the card number, card network, etc).

Returned tokens can then be passed around in accordance with PCI-DSS.

## Installation
### Maven users

Add this dependency to your project's POM:

```xml
<dependency>
  <groupId>com.wix.pay</groupId>
  <artifactId>smaug-client</artifactId>
  <version>1.7.0</version>
</dependency>
```

## Reporting Issues

Please use [the issue tracker](https://github.com/wix/smaug-client/issues) to report issues related to this library.

## License
This library uses the Apache License, version 2.0.
