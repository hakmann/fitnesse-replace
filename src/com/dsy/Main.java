package com.dsy;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Main {
    private static final String FILENAME_SUITE_SET_UP = "SuiteSetUp.wiki";
    private static final String FILENAME_SET_UP = "SetUp.wiki";

    public static void main(String[] args) throws IOException{
        if (args.length < 1) {
            System.out.print("Usage: \n" + "\tjava -jar fitnesse-replace.jar path1 path2 ...\n" + "\tpaths in args should be absolute.\n");
            System.exit(0);
        }

        CopyFolder cf = new CopyFolder();

        for(String s : args){
            String newFolder = cf.makeDestFolderName(s);
            cf.copyFolder(Paths.get(s), Paths.get(newFolder));

            Stream<Path> walk = Files.walk(Paths.get(newFolder));

            walk.filter(Files::isRegularFile).forEach(path ->{
                replaceSymbolicLink(path);

                if (FILENAME_SUITE_SET_UP.equals(String.valueOf(path.getFileName()))) {
                    replaceSuiteSetUp(path);
                }

                if (FILENAME_SET_UP.equals(String.valueOf(path.getFileName()))) {
                    replaceAccountSetUp(path);
                }
            });
        }
    }

    private static void replaceSuiteSetUp(Path path) {
        try{
            String contents = new String(Files.readAllBytes(path), Charset.defaultCharset());
            if (contents.indexOf("$personBornOn") > 0) {
                System.out.println("$personBornOn already exists in SuiteSetUp.wiki. skip");
            }else {
                String s = contents.replace("|$bornOn=       |value of|!weekDaysFromToday (yyyy-MM-dd) -7000|",
                        "|$bornOn=       |value of|!weekDaysFromToday (yyyy-MM-dd) -7000|\n|$personBornOn= |value of|!weekDaysFromToday (yyyy-MM-dd) -7000|");

                Files.write(path, s.getBytes());
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    private static void replaceSymbolicLink(Path path) {
        try{
            String contents = new String(Files.readAllBytes(path), Charset.defaultCharset());
            String s = contents.replace(".Vitality.SystemIntegrationTestSuites.JourneyTesting.Tenant_0.",
                    ".Vitality.SystemIntegrationTestSuites.JourneyTesting.Tenant_0_DB_Function.");

            Files.write(path, s.getBytes());

        }catch (IOException e){
            e.printStackTrace();
        }

    }

    private static void replaceAccountSetUp(Path path) {
        try{
            String contents = new String(Files.readAllBytes(path), Charset.defaultCharset());
            if (contents.indexOf("AccountSetUpWithFunction") > 0 ){
                System.out.println("Replace for AccountSetUpWithFunction has already been applied. skip");
            }else {
                String s = contents.replace("!include -c .Vitality.SystemIntegrationTestSuites.JourneyTesting.Common.AccountSetUp",
                        "!define tenant {$tenant}\n!include -c .Vitality.SystemIntegrationTestSuites.JourneyTesting.Common.AccountSetUpWithFunction");

                Files.write(path, s.replace("!define tenant {$tenant}\n!include -c .Vitality.SystemIntegrationTestSuites.JourneyTesting.Common.AccountSetUpWithFunction-IBE",
                        "!include -c .Vitality.SystemIntegrationTestSuites.JourneyTesting.Common.AccountSetUp")
                        .replace("!define tenant {$tenant}\n!include -c .Vitality.SystemIntegrationTestSuites.JourneyTesting.Common.AccountSetUpWithFunctionForAddressAndTelephone",
                                ".Vitality.SystemIntegrationTestSuites.JourneyTesting.Common.AccountSetUpForAddressAndTelephone").getBytes());
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
