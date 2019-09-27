package hr.yeti.rudimentary.cli;

import hr.yeti.rudimentary.cli.command.RunCommand;
import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.LinkOption.*;
import java.nio.file.attribute.*;
import java.io.*;
import java.util.*;

public class Watcher {

  private final WatchService watcher;
  private final Map<WatchKey, Path> keys;
  private final boolean recursive;
  private boolean trace = false;

  private RunCommand cmd;

  static <T> WatchEvent<T> cast(WatchEvent<?> event) {
    return (WatchEvent<T>) event;
  }

  private void register(Path dir) throws IOException {
    WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
    if (trace) {
      Path prev = keys.get(key);
      if (prev == null) {
        System.out.format("register: %s\n", dir);
      } else {
        if (!dir.equals(prev)) {
          System.out.format("update: %s -> %s\n", prev, dir);
        }
      }
    }
    keys.put(key, dir);
  }

  private void registerAll(final Path start) throws IOException {
    Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
          throws IOException {
        if (dir.startsWith("src")) {
          register(dir);
        }

        return FileVisitResult.CONTINUE;
      }
    });
  }

  public Watcher(Path dir, boolean recursive, RunCommand cmd) throws IOException {
    this.watcher = FileSystems.getDefault().newWatchService();
    this.keys = new HashMap<>();
    this.recursive = recursive;
    this.cmd = cmd;

    if (recursive) {
      registerAll(dir);
    } else {
      register(dir);
    }

    this.trace = true;
  }

  public void processEvents() throws IOException {
    for (;;) {

      WatchKey key;
      try {
        key = watcher.take();
      } catch (InterruptedException x) {
        return;
      }

      Path dir = keys.get(key);
      if (dir == null) {
        System.err.println("WatchKey not recognized!!");
        continue;
      }

      boolean found = false;
      for (WatchEvent<?> event : key.pollEvents()) {
        WatchEvent.Kind kind = event.kind();

        if (kind == OVERFLOW) {
          continue;
        }

        WatchEvent<Path> ev = cast(event);
        Path name = ev.context();
        Path child = dir.resolve(name);

        //System.out.format("%s: %s\n", event.kind().name(), child);
        if (recursive && (kind == ENTRY_CREATE)) {
          try {
            if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
              registerAll(child);
            }
          } catch (IOException e) {
            // Noop.
          }
        }
        found = true;
      }

      if (found && cmd.pid != null) {
        System.out.println("Changes detected...initiating reload....");
        
        ProcessHandle.of(Long.valueOf(cmd.pid)).get().destroy();
        cmd.consoleReader.setStop(true);
        cmd.pid = null;

        cmd.mavenRunRudyApplication();
        cmd.readProcessStdOut();
      }
      boolean valid = key.reset();
      if (!valid) {
        keys.remove(key);

        if (keys.isEmpty()) {
          break;
        }
      }
    }
  }

}
