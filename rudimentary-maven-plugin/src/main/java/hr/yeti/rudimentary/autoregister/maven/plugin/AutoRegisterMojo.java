package hr.yeti.rudimentary.autoregister.maven.plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "auto-register")
public class AutoRegisterMojo extends AbstractMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Path projectDir = new File("").toPath();
        Path sourcesDir = projectDir.resolve("src").resolve("main").resolve("java");

        try {
            Files.walkFileTree(sourcesDir, new RegisterAsServiceProvider());
        } catch (IOException ex) {
            Logger.getLogger(AutoRegisterMojo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
