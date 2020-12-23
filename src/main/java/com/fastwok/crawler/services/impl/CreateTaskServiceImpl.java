package com.fastwok.crawler.services.impl;

import com.fastwok.crawler.entities.User;
import com.fastwok.crawler.repository.UserRepository;
import com.fastwok.crawler.services.BaseService;
import com.fastwok.crawler.services.isservice.CreateTaskService;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class CreateTaskServiceImpl implements CreateTaskService {
    @Autowired
    UserRepository userRepository;
    //    @Value("${crawler.cron.delay.millisecond}")
    private String delay = "100000";
    private String urlUser="http://crm.htauto.vn/review/feedback/auth/";
    private String urlApartmentUser="http://crm.htauto.vn/review/feedback/apartment/auth/";
    @Override
    public void getData() throws UnirestException, MessagingException {
        Date date = new Date();
        long timeMilli = date.getTime();
        Date dateOfQuery = new Date(timeMilli - Integer.parseInt(delay) * 60000);
        SimpleDateFormat formatTimestamp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String strDateOfQuery = formatTimestamp.format(dateOfQuery);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        String dateTitleFW = format.format(date);
        log.info(strDateOfQuery);
        List<User> users = userRepository.getByDate(strDateOfQuery);
        createTaskUser(users,timeMilli, dateTitleFW,urlUser);
        List<User> apartmentUsers = userRepository.getByDateApartment(strDateOfQuery);
        createTaskUser(apartmentUsers,timeMilli, dateTitleFW,urlApartmentUser);
    }

    private void createTaskUser(List<User> users,long timeMilli, String startDate,String url) throws UnirestException {
        log.info(users.toString());
        for (User user : users) {
            String string = getBody(user, startDate, timeMilli, url);
            log.info(string);
            createTask(string, timeMilli);
        }
    }

    private HttpResponse<JsonNode> createTask(String string, Long timeMilli)
            throws UnirestException {
        return Unirest.post("https://work.fastwork.vn:6014/Task/5efef3dd5a51cf1c10fab0e4")
                .header("Accept", "*/*")
                .header("Authorization", "Basic dGhhbmd2dUBodGF1dG86N2Q1NzQ0YTI1NjM1MDE2Zjk4MzEyNjE1YWQyZWQzMzI=")
                .header("x-fw", String.valueOf(timeMilli))
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.125 Safari/537.36")
                .header("Accept-Language", "en-US,en;q=0.9,vi;q=0.8")
                .header("Referer", "https://app.fastwork.vn")
                .header("Origin", "https://app.fastwork.vn")
                .header("Host", "work.fastwork.vn:6014")
                .header("Connection", "keep-alive")
                .header("Accept-Encoding", "gzip, deflate, br")
                .header("Content-Type", "application/json")
                .header("Sec-Fetch-Dest", "empty")
                .header("Sec-Fetch-Mode", "cors")
                .header("Sec-Fetch-Site", "same-site")
                .body(string)
                .asJson();
    }

    private String getBody(User user, String date, Long from, String url) {
//        Date date = new Date();
//        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
//        String strDate = formatter.format(date);
//        String dateWithoutTime = strDate.toString().substring(0, 10);
        return "{\"worktype\":{}," +
                "\"score\":0," +
                "\"name\":\"BC 360/" + user.getName() + "/" + date + "\"," +
                "\"description\":\"Bạn bị đã bị feedback \\n Click vào đường link bên dưới để xác nhận:\\n" + url + user.getAuthentication() + "\"," +
                "\"assignTo\":[{\"email\":\"" + user.getEmailFastwork() + "\",\"name\":\"" + user.getName() + "\",\"avatar\":\"\"}]," +
                "\"followers\":[]," +
                "\"priority\":\"Trung bình\"," +
                "\"customers\":[]," +
                "\"contacts\":[]," +
                "\"forms\":[]," +
                "\"project\":{}," +
                "\"checklists\":[]," +
                "\"parent\":\"\"," +
                "\"start_date\":" + from + "," +
                "\"end_date\":" + (from + 172800000) + "," +
                "\"start\":\"\"," +
                "\"start_type\":\"no\"," +
                "\"deadline\":\"\"," +
                "\"deadline_type\":\"auto\"," +
                "\"members\":[{\"email\":\"assignTo\",\"name\":\"Người thực hiện\"}]," +
                "\"danh_gia\":false," +
                "\"viewMode\":\"protected\"," +
                "\"urgent\":false}";
    }
}
