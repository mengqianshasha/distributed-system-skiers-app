package servlet;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rabbitmq.client.Address;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import datamodel.Lift;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import rabbitmq.RMQChannelFactory;
import redis.RedisClient;
import utils.Utils;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@WebServlet("/servlet/*")
public class SkierServlet extends HttpServlet {
    private static final String HOST = "54.187.229.67";
    private static final int PORT = 5672;
    private static final String USERNAME = "user1";
    private static final String PASSWORD = "user123";
    private Connection connection;
    private GenericObjectPool pool;
    private static final String verticalDayPatternStr = "^\\/(\\d+)\\/seasons\\/(\\d+)\\/days\\/(\\d+)\\/skiers\\/(\\d+)$";
    private static final String verticalPatternStr = "^\\/(\\d+)\\/vertical$";
    public RedisClient redisClient;

    @Override
    public void init() {
        this.redisClient = new RedisClient();
        ConnectionFactory factory = new ConnectionFactory();
        Address[] adds = new Address[]{new Address(HOST, PORT)};
        factory.setUsername(USERNAME);
        factory.setPassword(PASSWORD);
        try {
            this.connection = factory.newConnection(adds);
            GenericObjectPoolConfig<Channel> config = new GenericObjectPoolConfig<>();
            config.setMaxIdle(200);
            this.pool = new GenericObjectPool<>(new RMQChannelFactory(this.connection), config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("text/plain");
        String urlPath = req.getPathInfo();
        Pattern verticalDayPattern = Pattern.compile(verticalDayPatternStr, Pattern.CASE_INSENSITIVE);
        Matcher verticalDayMatcher = verticalDayPattern.matcher(urlPath);
        Pattern verticalPattern = Pattern.compile(verticalPatternStr, Pattern.CASE_INSENSITIVE);
        Matcher verticalMatcher = verticalPattern.matcher(urlPath);

        String totalVertical = null;
        boolean isValid = false;
        if (verticalDayMatcher.find()) {
            isValid = true;
            totalVertical = this.redisClient.getTotalVerticalForSomeDay(
                    verticalDayMatcher.group(1),
                    verticalDayMatcher.group(2),
                    verticalDayMatcher.group(3),
                    verticalDayMatcher.group(4));
        } else if (verticalMatcher.find() && req.getParameter("resort") != null) {
            isValid = true;
            totalVertical = this.redisClient.getTotalVertical(verticalMatcher.group(1), req.getParameter("resort"), req.getParameter("season"));
        }

        if (isValid && totalVertical != null) {
            res.setStatus(200);
            res.getWriter().write(totalVertical);
        } else if (!isValid) {
            res.setStatus(400);
            res.getWriter().write("Invalid Input");
        } else {
            res.setStatus(404);
            res.getWriter().write("Data Not Found");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        JsonObject resObj = new JsonObject();
        PrintWriter out = res.getWriter();

        String urlPath = req.getPathInfo();
        BufferedReader bufferedReader = req.getReader();
        String bodyMsg = bufferedReader.lines().collect(Collectors.joining());
        if (!SkierValidator.urlExisting(urlPath)) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);        // 404
            resObj.addProperty("message", "Data not found");
        } else if (!SkierValidator.urlIsValid(urlPath) || !SkierValidator.bodyIsValid(bodyMsg)) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);      // 400
            resObj.addProperty("message", "Invalid inputs");
        } else {
            // push to queue
            try {
                Channel channel = (Channel) this.pool.borrowObject();
                Lift liftData = this.getLift(urlPath, bodyMsg);
                String liftMessage = new Gson().toJson(liftData);
                channel.basicPublish( RMQChannelFactory.EXCHANGE_NAME, RMQChannelFactory.BINDING_KEY, null, liftMessage.getBytes());
                this.pool.returnObject(channel);
                res.setStatus(HttpServletResponse.SC_CREATED);           // 201
                resObj.addProperty("message", "Write successful!");
            } catch (Exception e) {
                e.printStackTrace();
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resObj.addProperty("message", "Fail publish data");
            }
        }

        out.print(resObj);
    }

    private Lift getLift(String url, String body) {
        String[] urlPath = url.split("/");
        int resortId = Integer.parseInt(urlPath[1]);
        int seasonId = Integer.parseInt(urlPath[3]);
        int dayId = Integer.parseInt(urlPath[5]);
        int skierId = Integer.parseInt(urlPath[7]);

        JsonObject bodyJson = JsonParser.parseString(body).getAsJsonObject();
        JsonElement timeElement = bodyJson.get("time");
        int time = Integer.parseInt(Utils.getStringFromJsonElement(timeElement));
        JsonElement liftIdElement = bodyJson.get("liftID");
        int liftId = Integer.parseInt(Utils.getStringFromJsonElement(liftIdElement));

        return new Lift(resortId, seasonId, dayId, skierId, time, liftId);
    }
}
