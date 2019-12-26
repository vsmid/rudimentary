package hr.yeti.rudimentary.autoregister.maven.plugin.mojo;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Prints plugin version.
 * 
 * @author vedransmid@yeti-it.hr
 */
@Mojo(name = "version")
public class VersionMojo extends AbstractMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            Properties properties = new Properties();
            properties.load(getClass().getResourceAsStream("/META-INF/maven/hr.yeti.rudimentary/rudimentary-maven-plugin/pom.properties"));
            System.out.println(properties.getProperty("version"));
        } catch (IOException ex) {
            Logger.getLogger(VersionMojo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
