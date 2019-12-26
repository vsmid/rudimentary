package hr.yeti.rudimentary.autoregister.maven.plugin.mojo;

import hr.yeti.rudimentary.autoregister.maven.plugin.RegisterAsServiceProvider;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Auto registers all Rudimentary service providers.
 *
 * @author vedransmid@yeti-it.hr
 */
@Mojo(name = "register-providers", defaultPhase = LifecyclePhase.COMPILE)
public class RegisterProvidersMojo extends AbstractMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Path projectDir = new File("").toPath();
        Path sourcesDir = projectDir.resolve("src").resolve("main").resolve("java");

        try {
            Files.walkFileTree(sourcesDir, new RegisterAsServiceProvider());
        } catch (IOException ex) {
            Logger.getLogger(RegisterProvidersMojo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
