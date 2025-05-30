package com.example.simon.service.impl;

import com.example.simon.entity.DialogueSession;
import com.example.simon.entity.DialogueTurn;
import com.example.simon.mapper.DialogueSessionMapper;
import com.example.simon.mapper.DialogueTurnMapper;
import com.example.simon.service.DialogueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DialogueServiceImpl implements DialogueService {

    @Autowired
    private DialogueSessionMapper sessionMapper;

    @Autowired
    private DialogueTurnMapper turnMapper;

    @Override
    @Transactional
    public DialogueSession createSession(DialogueSession session) {
        sessionMapper.insert(session);
        return session;
    }

    @Override
    public DialogueSession getSessionById(Integer sessionId, Integer userId) {
        return sessionMapper.selectByIdAndUserId(sessionId, userId);
    }

    @Override
    public List<DialogueSession> getSessionsByUserId(Integer userId) {
        return sessionMapper.selectByUserId(userId);
    }

    @Override
    @Transactional
    public DialogueSession updateSession(DialogueSession session) {
        sessionMapper.update(session);
        return session;
    }

    @Override
    @Transactional
    public void deleteSession(Integer sessionId, Integer userId) {
        turnMapper.deleteBySessionId(sessionId, userId);
        sessionMapper.deleteById(sessionId, userId);
    }

    @Override
    @Transactional
    public DialogueTurn addTurn(DialogueTurn turn) {
        turnMapper.insert(turn);
        return turn;
    }

    @Override
    public DialogueTurn getTurnById(Integer turnId, Integer userId) {
        return turnMapper.selectByIdAndUserId(turnId, userId);
    }

    @Override
    public List<DialogueTurn> getTurnsBySessionId(Integer sessionId, Integer userId) {
        return turnMapper.selectBySessionIdAndUserId(sessionId, userId);
    }

    @Override
    @Transactional
    public DialogueTurn updateTurn(DialogueTurn turn, Integer userId) {
        // 获取原有的对话轮次以保留 createdAt
        DialogueTurn existingTurn = turnMapper.selectByIdAndUserId(turn.getTurnId(), userId);
        if (existingTurn != null) {
            turn.setCreatedAt(existingTurn.getCreatedAt());
        }
        turnMapper.update(turn.getTurnId(), userId, turn.getRawText(), turn.getCorrectedText());
        return turn;
    }

    @Override
    @Transactional
    public void deleteTurn(Integer turnId, Integer userId) {
        turnMapper.deleteById(turnId, userId);
    }
}