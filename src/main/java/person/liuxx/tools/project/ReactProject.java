package person.liuxx.tools.project;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import person.liuxx.util.base.StringUtil;
import person.liuxx.util.log.LogUtil;

/**
 * @author 刘湘湘
 * @version 1.0.0<br>
 *          创建时间：2018年4月13日 下午2:05:48
 * @since 1.0.0
 */
public class ReactProject
{
    private Logger log = LoggerFactory.getLogger(ReactProject.class);
    private final String LF = new String(Character.toChars(0x0A));
    private String projectName;
    private Path path;
    private String description;
    private String license;

    public ReactProject(String projectName, Path path)
    {
        this.projectName = projectName;
        this.path = path;
        this.description = "";
        this.license = "MIT";
    }

    private InputStream packageJsonInputStream() throws IOException
    {
        String packageJson = Files.lines(path.resolve("package.json"))
                .filter(line -> !StringUtil.isBlank(line))
                .map(line ->
                {
                    if (line.contains("name\":"))
                    {
                        int index = line.indexOf("\": \"") + 4;
                        return line.substring(0, index) + projectName + "\",";
                    }
                    if (line.contains("description\":"))
                    {
                        int index = line.indexOf("\": \"") + 4;
                        return line.substring(0, index) + description + "\",";
                    }
                    if (line.contains("license\":"))
                    {
                        int index = line.indexOf("\": \"") + 4;
                        return line.substring(0, index) + license + "\"";
                    }
                    return line;
                })
                .collect(Collectors.joining(LF));
        return new ByteArrayInputStream(packageJson.getBytes("UTF-8"));
    }

    public ByteArrayOutputStream createZipOutputStream() throws IOException
    {
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        ZipOutputStream zout = new ZipOutputStream(o);
        Files.walk(path).forEach(f ->
        {
            createZip(f, zout);
        });
        return o;
    }

    private void createZip(Path p, ZipOutputStream zout)
    {
        boolean isFile = !Files.isDirectory(p);
        String fileName = path.relativize(p).toString() + (isFile ? "" : "/");
        if (Objects.equals(fileName, "/"))
        {
            return;
        }
        log.debug("fileName:{}", fileName);
        try
        {
            ZipEntry ze = new ZipEntry(fileName);
            zout.putNextEntry(ze);
            if (isFile)
            {
                try (InputStream fis = createInputStream(p);)
                {
                    int j = 0;
                    byte[] buffer = new byte[1024];
                    while ((j = fis.read(buffer)) > 0)
                    {
                        zout.write(buffer, 0, j);
                    }
                }
            }
            zout.closeEntry();
        } catch (IOException e)
        {
            log.error(LogUtil.errorInfo(e));
        }
    }

    private InputStream createInputStream(Path p) throws IOException
    {
        switch (p.getFileName().toString())
        {
        case "package.json":
            {
                return packageJsonInputStream();
            }
        }
        return Files.newInputStream(p);
    }
}
