package org.github.zymosi3.mg;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class FileStreamer implements Supplier<Stream<Consumer<Drawer>>> {

    public static FileStreamer ofResource(String resource) {
        URL url = FileStreamer.class.getClassLoader().getResource(resource);
        assert url != null;
        return new FileStreamer(FileSystems.getDefault().getPath(url.getPath()));
    }

    private final Path path;

    private FileStreamer(Path path) {
        this.path = path;
    }

    @Override
    public Stream<Consumer<Drawer>> get() {
        try {
            return Files.lines(path).
                    map(String::trim).
                    filter(s -> !"".equals(s)).
                    filter(s -> !s.startsWith("//")).
                    map(s -> s.split(":")).
                    map(a -> chooseType(a[0].trim(), a[1].split(",")));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Consumer<Drawer> chooseType(String type, String[] params) {
        switch (type) {
            case "l":
                int[] intParams = Stream.of(params).
                        map(String::trim).
                        map(Integer::parseInt).
                        mapToInt(i -> i).
                        toArray();
                return drawer -> drawer.line(
                        intParams[0],
                        intParams[1],
                        intParams[2],
                        intParams[3],
                        Drawer.color(intParams[4], intParams[5], intParams[6])
                );
            default:
                throw new IllegalArgumentException("Unknown type: " + type);
        }
    }
}
