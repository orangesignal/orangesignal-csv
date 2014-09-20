# [OrangeSignal CSV](http://orangesignal.github.io/orangesignal-csv/) [![Build Status](https://travis-ci.org/orangesignal/orangesignal-csv.png?branch=master)](https://travis-ci.org/orangesignal/orangesignal-csv)

OrangeSignal CSV is a very flexible csv (comma-separated values) read and write library for Java.  

The binary distributions includes the following third party software:  
[jLHA (LHA Library for Java)](http://homepage1.nifty.com/dangan/en/Content/Program/Java/jLHA/jLHA.html).

## Prerequisites

* Java 1.6+  
OrangeSignal CSV is compiled for Java 1.6

## Installation

### Maven users

If you are using Maven, simply copy the following dependency into your pom.xml file. The artifact is hosted at [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Corangesignal-csv), and is standalone (no dependencies).

```xml
<dependency>
    <groupId>com.orangesignal</groupId>
    <artifactId>orangesignal-csv</artifactId>
    <version>2.2.1</version>
</dependency>
```

## Examples

CSV entity class

```java
@CsvEntity(header = true)
public class Customer {

    @CsvColumn(name = "name")
    public String name;

    @CsvColumn(name = "age")
    public Integer age;

}
```

example code


```java
CsvConfig cfg = new CsvConfig(',', '"', '"');
cfg.setNullString("NULL");
cfg.setIgnoreLeadingWhitespaces(true);
cfg.setIgnoreTrailingWhitespaces(true);
cfg.setIgnoreEmptyLines(true);
cfg.setIgnoreLinePatterns(Pattern.compile("^#.*$"));
cfg.setVariableColumns(false);

List<Customer> list = new CsvEntityManager()
    .config(cfg)
    .load(Customer.class)
    .filter(new SimpleBeanFilter().in("name", "Smith", "Johnson").gt("age", 21))
    .offset(10)
    .limit(1000)
    .order(BeanOrder.desc("age"))
    .from(reader);
```

## How to use

* [User guide](http://orangesignal.github.io/orangesignal-csv/userguide.html)
* [Migration](http://orangesignal.github.io/orangesignal-csv/migration.html)

Sorry, it is japanese only for now.

## License

* Licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).
