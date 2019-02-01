package bbro.mkreq.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class RequestController {

    @Autowired
    private RequestService requestService;

    @GetMapping("res")
    public String res(HttpServletRequest request){
        return requestService.res(request);
    }

    @GetMapping("initThreadStart")
    public String threadStart() {
       return requestService.threadStart();
    }

    @GetMapping("newThread/{id}")
    public String newThread(@PathVariable int id) {
        return requestService.newThread(id);
    }
}
