package quest.controller.net.tcp;

import static quest.controller.log.QLog.MsgType.ERROR;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import quest.controller.log.QLog;

abstract class ProHandler implements HttpHandler {
	Map<String, List<String>> POST;
	Map<String, List<String>> GET;
	StringBuilder responseStringBuilder = new StringBuilder();

	@Override
	public void handle(HttpExchange t) {
		BufferedReader br = new BufferedReader(new InputStreamReader(t.getRequestBody()));
		this.POST = QuestHttpServer.decodeRawQuery(br.lines().collect(Collectors.joining()));
		this.GET = QuestHttpServer.decodeRawQuery(t.getRequestURI().getRawQuery());
		try {
			handlePro(t);
		} catch (IOException e) {
			QLog.inst().print(e.getLocalizedMessage(), ERROR);
		}
	}

	public abstract void handlePro(HttpExchange t) throws IOException;

}