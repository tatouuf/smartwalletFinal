import java.net.*;
import java.io.*;
import java.nio.channels.*;
import java.util.Properties;

public class MavenWrapperDownloader {

    private static final String WRAPPER_VERSION = "3.2.0";

    public static void main(String... args) {
        System.out.println("- Downloading maven-wrapper from: " + getDownloadUrl());
        File baseDirectory = new File(args[0]);
        System.out.println("- Base directory: " + baseDirectory.getAbsolutePath());

        File mavenWrapperPropertyFile = new File(baseDirectory, ".mvn/wrapper/maven-wrapper.properties");
        String url = getDownloadUrl();

        if (mavenWrapperPropertyFile.exists()) {
            FileInputStream mavenWrapperPropertyFileInputStream = null;
            try {
                mavenWrapperPropertyFileInputStream = new FileInputStream(mavenWrapperPropertyFile);
                Properties mavenWrapperProperties = new Properties();
                mavenWrapperProperties.load(mavenWrapperPropertyFileInputStream);
                url = mavenWrapperProperties.getProperty("distributionUrl", url);
            } catch (IOException e) {
                System.out.println("- ERROR loading '" + mavenWrapperPropertyFile + "'");
            } finally {
                try {
                    if (mavenWrapperPropertyFileInputStream != null)
                        mavenWrapperPropertyFileInputStream.close();
                } catch (IOException e) {
                }
            }
        }
        System.out.println("- Downloading from: " + url);

        File outputFile = new File(baseDirectory.getAbsolutePath(), ".mvn/wrapper/maven-wrapper.jar");
        if (!outputFile.getParentFile().exists() && !outputFile.getParentFile().mkdirs()) {
            System.out.println(
                    "- ERROR: Could not create directory: " + outputFile.getParentFile().getAbsolutePath());
            System.exit(1);
        }
        System.out.println("- Downloading to: " + outputFile.getAbsolutePath());
        try {
            downloadFileFromURL(url, outputFile);
            System.out.println("Done");
            System.exit(0);
        } catch (Throwable e) {
            System.out.println("- ERROR downloading '" + url + "'");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void downloadFileFromURL(String urlString, File destination) throws Exception {
        if (System.getenv("MVNW_REPOURL") != null) {
            String repoUrl = System.getenv("MVNW_REPOURL");
            urlString = repoUrl + "/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar";
        }
        URL website = new URL(urlString);
        ReadableByteChannel rbc;
        rbc = Channels.newChannel(website.openStream());
        FileOutputStream fos = new FileOutputStream(destination);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
    }

    private static String getDownloadUrl() {
        return "https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar";
    }
}

