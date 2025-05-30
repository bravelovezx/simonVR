package com.example.simon.service;

import com.example.simon.entity.DialogueSession;
import com.example.simon.entity.DialogueTurn;

import java.util.List;

public interface DialogueService {
    // Session operations
    DialogueSession createSession(DialogueSession session);

    DialogueSession getSessionById(Integer sessionId, Integer userId);

    List<DialogueSession> getSessionsByUserId(Integer userId);

    DialogueSession updateSession(DialogueSession session);

    void deleteSession(Integer sessionId, Integer userId);

    // Turn operations
    DialogueTurn addTurn(DialogueTurn turn);

    DialogueTurn getTurnById(Integer turnId, Integer userId);

    List<DialogueTurn> getTurnsBySessionId(Integer sessionId, Integer userId);

    DialogueTurn updateTurn(DialogueTurn turn, Integer userId);

    void deleteTurn(Integer turnId, Integer userId);
}