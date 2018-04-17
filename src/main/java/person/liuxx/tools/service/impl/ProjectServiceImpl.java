package person.liuxx.tools.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import person.liuxx.tools.config.ElConfig;
import person.liuxx.tools.project.ReactProject;
import person.liuxx.tools.service.ProjectService;
import person.liuxx.util.log.LogUtil;

/**
 * @author 刘湘湘
 * @version 1.0.0<br>
 *          创建时间：2018年3月30日 下午4:28:42
 * @since 1.0.0
 */
@Service
public class ProjectServiceImpl implements ProjectService
{
    private Logger log = LoggerFactory.getLogger(ProjectServiceImpl.class);
    @Autowired
    private ElConfig elConfig;

    @Override
    public ResponseEntity<Resource> springbootProject()
    {
        ResponseEntity<Resource> result = null;
        Path file = Paths.get("");
        Resource resource = null;
        try
        {
            resource = new UrlResource(file.toUri());
            result = ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''"
                            + URLEncoder.encode("合并后的表格.xlsx", "UTF-8") + "")
                    .body(resource);
        } catch (MalformedURLException | UnsupportedEncodingException e)
        {
            log.error("获取资源时发生异常：" + e);
        }
        return result;
    }

    @Override
    public ResponseEntity<Resource> reactProject(HttpServletRequest request)
    {
        String projectName = "aa";
        ResponseEntity<Resource> result = null;
        Optional<ReactProject> op = Optional.ofNullable(elConfig)
                .flatMap(el -> el.reactProjectPath())
                .map(p -> new ReactProject(projectName, "测试项目", p));
        try
        {
            PipedInputStream in = new PipedInputStream();
            final PipedOutputStream out = new PipedOutputStream(in);
            if (op.isPresent())
            {
                new Thread(() ->
                {
                    try
                    {
                        out.write(op.get().createZipOutputStream().toByteArray());
                        out.close();
                    } catch (IOException e)
                    {
                        log.error(LogUtil.errorInfo(e));
                    }
                }).start();
                return resourceReponse(in, projectName + ".zip");
            }
        } catch (IOException e)
        {
            log.error(LogUtil.errorInfo(e));
        }
        return result;
    }

    private ResponseEntity<Resource> resourceReponse(InputStream in, String fileName)
            throws UnsupportedEncodingException
    {
        Resource resource = new InputStreamResource(in);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''"
                        + URLEncoder.encode(fileName, "UTF-8"))
                .body(resource);
    }
}
