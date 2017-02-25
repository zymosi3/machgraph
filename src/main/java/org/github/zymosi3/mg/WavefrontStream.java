package org.github.zymosi3.mg;

import org.github.zymosi3.mg.math.Vec3;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 */
public class WavefrontStream implements Supplier<Stream<Face>> {

    public static WavefrontStream ofResource(String resource) {
        URL url = FileStreamer.class.getClassLoader().getResource(resource);
        assert url != null;
        return new WavefrontStream(FileSystems.getDefault().getPath(url.getPath()));
    }

    private final Path path;

    private WavefrontStream(Path path) {
        this.path = path;
    }

    @Override
    public Stream<Face> get() {
        try {
            List<String> lines =
                    Files.lines(path).
                            filter(l -> !"".equals(l.trim())).
                            filter(l -> !l.startsWith("//")).
                            filter(l -> !l.startsWith("#")).
                            collect(Collectors.toList());

            Vec3[] vertices = lines.stream().
                    filter(l -> l.startsWith("v ")).
                    map(l -> Stream.of(l.split(" ")).
                            skip(1).
                            map(String::trim).
                            map(Float::parseFloat).
                            mapToDouble(f -> f).
                            toArray()).
                    map(a -> new Vec3((float) a[0], (float) a[1], (float) a[2])).
                    toArray(Vec3[]::new);

            return lines.stream().
                    filter(l -> l.startsWith("f ")).
                    map(l -> Stream.of(l.split(" ")).
                            skip(1).
                            map(String::trim).
                            map(s -> s.split("/")[0]).
                            map(String::trim).
                            map(Integer::parseInt).
                            mapToInt(i -> i - 1).
                            toArray()).
                    map(a -> new Face(Arrays.stream(a).mapToObj(i -> vertices[i]).toArray(Vec3[]::new)));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
