package dk.bemyndigelsesregister.bemyndigelsesservice.web;

import dk.bemyndigelsesregister.bemyndigelsesservice.server.dao.AuthorizationDao;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;

@RequestMapping("/authorization")
@Controller
public class AuthorizationController {
    @Inject
    AuthorizationDao authorizationDao;

    @RequestMapping("/name") @ResponseBody
    public String getNameFor(@RequestParam long id) {
        return authorizationDao.get(id).getName();
    }
}
