package edu.csula.rubrics.util;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import javax.persistence.Persistence;

import org.hibernate.engine.jdbc.internal.DDLFormatterImpl;
import org.hibernate.engine.jdbc.internal.Formatter;

public class SchemaExport {

  public static void main(String args[]) throws IOException {
    // Write the generated schema to a string
    StringWriter stringWriter = new StringWriter();
    Map<String, Object> properties = new HashMap<>();
    properties.put("javax.persistence.schema-generation.scripts.action", "create");
    properties.put("javax.persistence.schema-generation.scripts.create-target", stringWriter);
    Persistence.generateSchema("rubricPrj", properties);

    // If there is a command line argument, consider it the output file name
    BufferedWriter out = null;
    if (args.length > 0)
      out = new BufferedWriter(new FileWriter(args[0]));

    // Use Hibernate's SQL formatter to format each statement
    Formatter formatter = new DDLFormatterImpl();
    Scanner scanner = new Scanner(stringWriter.toString());
    while (scanner.hasNextLine()) {
      String line = formatter.format(scanner.nextLine()) + ";";
      System.out.println(line);
      if (out != null) {
        out.write(line);
        out.newLine();
      }
    }

    scanner.close();
    if (out != null)
      out.close();
  }

}