package bbro.mkreq.request;

import bbro.mkreq.webUrl.WebUrl;
import bbro.mkreq.webUrl.WebUrlService;
import org.hibernate.StaleObjectStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class RequestService {

    @Autowired
    private WebUrlService webUrlService;

    public String res(HttpServletRequest request){
        System.out.println("res get -- request info:");
        System.out.println(request.getRemoteAddr());
        return "res";
    }

    public String threadStart() {

        List<WebUrl> urlList = webUrlService.getAll();
        for (WebUrl webUrl: urlList){
            System.out.println("after for id: "+ webUrl.getId());
            //threads per url
            new Thread(()->{
                System.out.println("Thread is started for url: " + webUrl.getUrl());
                for (int j = 0;j<webUrl.getIterationLimit();j++){

                    String uri = webUrl.getUrl();

                    webUrl.iterationIncrement();

                    System.out.println("---------------\nclient uri: "+uri +"\niteration: "
                            +webUrl.getIterationAmount()+"\nlimit: "+ webUrl.getIterationLimit());

                    RestTemplate restTemplate = new RestTemplate();

                    try {
                        //Main work for request--------------------------------------------------
                        String result = restTemplate.getForObject(uri, String.class);
                        System.out.println("before save id: "+ webUrl.getId());

                        System.out.println(webUrlService.saveUrl(webUrl));


                        System.out.println(result);

                        Thread.sleep((long) ((3600*1000)/webUrl.getReqPerHour()));

                        //end of main work-------------------------------------------------------

                    }catch (IllegalArgumentException e){
                        e.printStackTrace();
                        webUrlService.deleteUrl(webUrl);
                        break;
                    } catch (StaleObjectStateException e){
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                webUrlService.deleteUrl(webUrl);


            }).start();


        }
        return "All initial threads are has been started";
    }

    public String newThread(int id) {

        WebUrl webUrl= webUrlService.finById(id);


        //new thread for new url
        new Thread(()->{
            System.out.println("New Thread is started for new url: " + webUrl.getUrl());
            for (int k = 0; k <webUrl.getIterationLimit(); k++){

                String uri = webUrl.getUrl();

                webUrl.iterationIncrement();

                System.out.println("----------------\nNew client uri: "+uri +"\niteration: "
                        +webUrl.getIterationAmount()+"\nlimit: "+ webUrl.getIterationLimit());

                RestTemplate restTemplate = new RestTemplate();

                try {
                    //Main work for request---------------------------------------------------
                    String result = restTemplate.getForObject(uri, String.class);
                    System.out.println("New before save id:  "+ webUrl.getId());

                    System.out.println(webUrlService.saveUrl(webUrl));


                    System.out.println(result);

                    Thread.sleep((long) ((3600*1000)/webUrl.getReqPerHour()));

                    //end of main work-------------------------------------------------------

                }catch (IllegalArgumentException e){
                    e.printStackTrace();
                    webUrlService.deleteUrl(webUrl);
                    break;
                } catch (StaleObjectStateException e){
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            webUrlService.deleteUrl(webUrl);


        }).start();


        return "new Thread has been started";
    }

}
