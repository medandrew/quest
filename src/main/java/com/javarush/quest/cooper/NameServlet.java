package com.javarush.quest.cooper;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.annotation.WebServlet;
import com.javarush.quest.cooper.entity.Quest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.javarush.quest.cooper.constants.QuestConstants;

import static org.apache.commons.lang3.StringUtils.isBlank;

@WebServlet(name = "NameServlet", value = "/name")
public class NameServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        HttpSession currentSession = req.getSession(true);

        Quest quest = Quest.getQuest(currentSession);
        quest.setName(getName(req));

        currentSession.setAttribute(QuestConstants.QUEST, quest);
        currentSession.setAttribute(QuestConstants.LEVEL, 0);

        resp.sendRedirect("/index.jsp");
    }

    String getName(HttpServletRequest request) {
        String name = getParamFromRequest(request, QuestConstants.NAME);
        return !isBlank(name) ? name : QuestConstants.DEFAULT_NAME;
    }

    String getParamFromRequest(HttpServletRequest request, String paramName) {
        return request.getParameter(paramName);
    }
}
