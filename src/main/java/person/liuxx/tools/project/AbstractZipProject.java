package person.liuxx.tools.project;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import person.liuxx.util.log.LogUtil;

/**
 * @author 刘湘湘
 * @version 1.0.0<br>
 *          创建时间：2018年4月16日 下午4:14:14
 * @since 1.0.0
 */
public abstract class AbstractZipProject implements ZipProject
{
    private Logger log = LoggerFactory.getLogger(AbstractZipProject.class);

    public ByteArrayOutputStream createZipOutputStream() throws IOException
    {
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        ZipOutputStream zout = new ZipOutputStream(o);
        Files.walk(path()).forEach(f ->
        {
            createZip(f, zout);
        });
        return o;
    }

    private void createZip(Path p, ZipOutputStream zout)
    {
        log.debug("Path:{}",p);
        boolean isFile = !Files.isDirectory(p);
        String fileName = path().relativize(p).toString() + (isFile ? "" : "/");
        log.debug("fileName:{}",fileName);
        if (Objects.equals(fileName, "/"))
        {
            return;
        }
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

    protected abstract InputStream createInputStream(Path p) throws IOException;

    protected abstract Path path();
}
