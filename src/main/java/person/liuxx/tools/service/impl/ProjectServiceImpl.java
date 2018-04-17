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

import javax.servlet.http.HttpSession;

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
import person.liuxx.tools.dto.ReactProjectDTO;
import person.liuxx.tools.project.ReactProject;
import person.liuxx.tools.service.ProjectService;
import person.liuxx.util.log.LogUtil;
import person.liuxx.util.service.reponse.EmptySuccedResponse;

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
    private static final String REACT_PROJECT = "React";
    private static final String SPRING_BOOT_PROJECT = "SpringBoot";
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

    private ResponseEntity<Resource> resourceReponse(InputStream in, String fileName)
            throws UnsupportedEncodingException
    {
        Resource resource = new InputStreamResource(in);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''"
                        + URLEncoder.encode(fileName, "UTF-8"))
                .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                .body(resource);
    }

    @Override
    public Optional<EmptySuccedResponse> createSessionReactProject(ReactProjectDTO project,
            HttpSession session)
    {
        Optional<ReactProject> op = Optional.ofNullable(elConfig)
                .flatMap(el -> el.reactProjectPath())
                .map(p -> project.mapToProject(p));
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
                ResponseEntity<Resource> r = resourceReponse(in, op.get().getProjectName()
                        + ".zip");
                session.setAttribute(REACT_PROJECT, r);
                return Optional.of(new EmptySuccedResponse());
            }
        } catch (IOException e)
        {
            log.error(LogUtil.errorInfo(e));
        }
        return Optional.empty();
    }

    @Override
    public Optional<ResponseEntity<Resource>> getReactProject(HttpSession session)
    {
        @SuppressWarnings("unchecked")
        ResponseEntity<Resource> r = (ResponseEntity<Resource>) session.getAttribute(REACT_PROJECT);
        session.removeAttribute(REACT_PROJECT);
        return Optional.ofNullable(r);
    }
}
