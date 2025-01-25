package ro.eduardismund.flightmgmt.app;

import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.Scanner;
import ro.eduardismund.flightmgmt.cli.CliManager;

public class CliManagerComponentFactory implements ComponentFactory<CliManager> {

    @Override
    public CliManager createComponent(Properties properties, ComponentResolver componentResolver) {
        return new CliManager(System.out, new Scanner(System.in, StandardCharsets.UTF_8));
    }
}
