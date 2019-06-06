package person.liuxx.tools.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import person.liuxx.tools.config.ElConfig;
import person.liuxx.tools.dto.ReactProjectDTO;
import person.liuxx.tools.dto.SpringBootProjectDTO;
import person.liuxx.tools.project.ZipProject;
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
    private static final int MAX_THREAD_NUMBER = 10;
    private AtomicInteger threadNumber = new AtomicInteger(0);
    @Autowired
    private ElConfig elConfig;

    @Override
    public Optional<EmptySuccedResponse> createSessionReactProject(ReactProjectDTO project,
            HttpSession session)
    {
        return Optional.ofNullable(elConfig)
                .flatMap(el -> el.reactProjectPath())
                .map(p -> project.mapToProject(p))
                .flatMap(p -> createSessionProject(p, session, REACT_PROJECT));
    }

    @Override
    public Optional<ResponseEntity<Resource>> getReactProject(HttpSession session)
    {
        return getSessionResource(session, REACT_PROJECT);
    }

    @Override
    public Optional<EmptySuccedResponse> updateSpringBootProject(SpringBootProjectDTO project)
    {
        return Optional.ofNullable(elConfig)
                .flatMap(el -> el.springbootProjectPath())
                .map(p -> project.mapToProject(p))
                .map(p ->
                {
                    try
                    {
                        p.update();
                        return new EmptySuccedResponse();
                    } catch (IOException e)
                    {
                        log.error(LogUtil.errorInfo(e));
                        return null;
                    }
                });
    }

    private Optional<EmptySuccedResponse> createSessionProject(ZipProject p, HttpSession session,
            String key)
    {
        try
        {
            PipedInputStream in = new PipedInputStream();
            final PipedOutputStream out = new PipedOutputStream(in);
            if (threadNumber.addAndGet(1) < MAX_THREAD_NUMBER)
            {
                new Thread(() ->
                {
                    try
                    {
                        out.write(p.createZipOutputStream().toByteArray());
                        out.close();
                    } catch (IOException e)
                    {
                        log.error(LogUtil.errorInfo(e));
                    } finally
                    {
                        threadNumber.getAndDecrement();
                    }
                }).start();
                ResponseEntity<Resource> r = resourceReponse(in, p.getProjectName() + ".zip");
                session.setAttribute(key, r);
                return Optional.of(new EmptySuccedResponse());
            }
        } catch (IOException e)
        {
            log.error(LogUtil.errorInfo(e));
        }
        return Optional.empty();
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

    private Optional<ResponseEntity<Resource>> getSessionResource(HttpSession session, String key)
    {
        @SuppressWarnings("unchecked")
        ResponseEntity<Resource> r = (ResponseEntity<Resource>) session.getAttribute(key);
        session.removeAttribute(key);
        return Optional.ofNullable(r);
    }
}
