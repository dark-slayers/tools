package person.liuxx.tools.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import person.liuxx.tools.service.ProjectService;

/**
 * @author 刘湘湘
 * @version 1.0.0<br>
 *          创建时间：2018年3月30日 下午4:10:21
 * @since 1.0.0
 */
@Controller
public class ProjectController
{
    @Autowired
    private ProjectService projectService;

    @GetMapping(value = "/project/springboot/zipfile")
    @ResponseBody
    public ResponseEntity<Resource> springbootProject()
    {
        return projectService.springbootProject();
    }

    @GetMapping(value = "/project/react/zipfile")
    @ResponseBody
    public ResponseEntity<Resource> reactProject(HttpServletRequest request)
    {

        return projectService.reactProject(request);
    }
}
