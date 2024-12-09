package com.matezalantoth.codeconverse.service;

import com.matezalantoth.codeconverse.exception.NotFoundException;
import com.matezalantoth.codeconverse.model.tag.dtos.AutocompleteResult;
import com.matezalantoth.codeconverse.model.tag.dtos.NewTagDTO;
import com.matezalantoth.codeconverse.model.tag.Tag;
import com.matezalantoth.codeconverse.model.tag.dtos.TagDTO;
import com.matezalantoth.codeconverse.model.tag.dtos.TagWithoutQuestionDTO;
import com.matezalantoth.codeconverse.repository.TagRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Service
public class TagService {

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public TagDTO createTag(NewTagDTO newTag) {
        Tag tag = new Tag();
        tag.setName(newTag.name());
        tag.setDescription(newTag.description());
        tag.setQuestionTags(new HashSet<>());
        tagRepository.save(tag);
        return tag.dto();
    }

    public TagWithoutQuestionDTO getTag(UUID id) {
        return tagRepository.getTagById(id).orElseThrow(() -> new NotFoundException("tag of id: " + id)).dtoNoQuestions();
    }

    //maybe filter first
    public List<AutocompleteResult> getMatchingTags(String substring, Set<AutocompleteResult> chosenTags) {
        if(substring.isEmpty()) {
            return new ArrayList<>();
        }

        var chosenTagIds = chosenTags.stream()
                .map(t -> t.tag().id())
                .collect(Collectors.toSet());

        var tags = tagRepository.findAll();
        var substringArray = substring.toCharArray();

        return tags.stream().filter(t -> !chosenTagIds.contains(t.getId())).map(t -> {
                    int currentSubstringIndex = 0;
                    HashMap<Integer, Integer> value = new HashMap<>();
                    char[] characters = t.getName().toLowerCase().toCharArray();
                    for (int i = 0; i < characters.length; i++) {
                        if (currentSubstringIndex == substring.length()) {
                            currentSubstringIndex = 0;
                        }
                        if (i == 0 && characters[0] == substringArray[0]) {
                            value.put(i, 100);
                            currentSubstringIndex++;
                            continue;
                        }
                        if (characters[i] == substringArray[currentSubstringIndex]) {
                            if (value.containsKey(i - 1)) {
                                value.put(i, value.get(i - 1) * 2);
                                currentSubstringIndex++;
                                continue;
                            }
                            value.put(i, 25);
                            currentSubstringIndex++;
                        }
                    }
                    var valueAdded = substring.length() == t.getName().length() ? 100 : 0;
                    return new AutocompleteResult(t.dtoNoQuestions(),
                            value.values()
                                    .stream()
                                    .mapToInt(i -> i).sum() + valueAdded);
                })
                .sorted(Comparator.comparingInt(AutocompleteResult::score).reversed())
                .limit(8)
                .toList();
    }
}
