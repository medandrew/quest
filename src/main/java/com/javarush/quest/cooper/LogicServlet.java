package com.javarush.quest.cooper;

import java.io.IOException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.annotation.WebServlet;
import com.javarush.quest.cooper.entity.Quest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.javarush.quest.cooper.entity.QuestOption;
import com.javarush.quest.cooper.exceptions.AppException;
import com.javarush.quest.cooper.constants.QuestConstants;
import static org.apache.commons.lang3.StringUtils.isBlank;

@WebServlet(name = "LogicServlet", value = "/logic")
public class LogicServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession currentSession = req.getSession();

        try {
            int level = getLevel(currentSession);
            int answer = getAnswer(req);
            Quest quest = Quest.getQuest(currentSession);
            QuestOption option = quest.getQuests().get(level).getOptions().get(answer);

            if (option.isWin()) quest.setWin(true); // winner
            if (option.isLose()) quest.setLose(true); // loser

            currentSession.setAttribute(QuestConstants.ERROR, null);
            currentSession.setAttribute(QuestConstants.QUEST, quest);
            currentSession.setAttribute(QuestConstants.ANSWER, answer);
            currentSession.setAttribute(QuestConstants.LEVEL, !option.isWin() && !option.isLose() ? level + 1 : level);
        } catch (AppException e) {
            currentSession.setAttribute(QuestConstants.ERROR, e.getMessage());
        }

        resp.sendRedirect("/index.jsp");
    }

    int getLevel(HttpSession currentSession) throws AppException {
        int level = 0;
        Object obj = currentSession.getAttribute(QuestConstants.LEVEL);

        if (obj != null) {
            if (java.lang.Integer.class != obj.getClass()) {
                currentSession.invalidate();
                throw new AppException(QuestConstants.SESSION_IS_BROKEN);
            }

            level = (int) obj;
        }

        return level;
    }

    int getAnswer(HttpServletRequest request) throws AppException {
        return getParamFromRequest(request, QuestConstants.ANSWER);
    }

    int getParamFromRequest(HttpServletRequest request, String paramName) throws AppException {
        String paramValue = request.getParameter(paramName);
        if (!isNumeric(paramValue)) throw new AppException(QuestConstants.UNSUPPORTED_VALUE);
        return Integer.parseInt(paramValue);
    }

    boolean isNumeric(String param) {
        return !isBlank(param) && param.chars().allMatch(Character::isDigit);
    }
}
