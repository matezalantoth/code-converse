package com.matezalantoth.codeconverse.controller.answercontrollertests;

import com.matezalantoth.codeconverse.model.answer.dtos.AnswerDTO;
import com.matezalantoth.codeconverse.model.answer.dtos.NewAnswerDTO;
import com.matezalantoth.codeconverse.model.vote.VoteType;
import com.matezalantoth.codeconverse.model.vote.dtos.NewVoteDTO;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;

@Transactional
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class VoteAnswerTests extends AnswerControllerTestBase {

    public VoteAnswerTests(@Autowired TestRestTemplate testTemplate) {
        super(testTemplate);
    }

    @Test
    void upvoteAnswerTest(){
        createAndVote(VoteType.UPVOTE, 1, 1, false);
    }

    @Test
    void doubleVoteResultsInZeroChangeFromOriginal(){
        createAndVote(VoteType.UPVOTE, 1, 2, false);
    }

    @Test
    void downvoteAnswerTest(){
        createAndVote(VoteType.DOWNVOTE, -1, 1, false);
    }
}
