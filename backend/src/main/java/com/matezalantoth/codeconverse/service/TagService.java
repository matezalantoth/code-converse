package com.matezalantoth.codeconverse.service;

import com.matezalantoth.codeconverse.exception.NotFoundException;
import com.matezalantoth.codeconverse.model.question.Question;
import com.matezalantoth.codeconverse.model.question.dtos.PaginationDTO;
import com.matezalantoth.codeconverse.model.question.dtos.QuestionsResponseDTO;
import com.matezalantoth.codeconverse.model.tag.dtos.*;
import com.matezalantoth.codeconverse.model.tag.Tag;
import com.matezalantoth.codeconverse.repository.TagRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;
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

    public TagPageDTO getTags(int startIndex) {
        var tags = tagRepository.findAll();
        if (tags.isEmpty()) {
            return new TagPageDTO(new PaginationDTO(startIndex, 1, 1), new HashSet<>());
        }
        var sorted = tags.stream().sorted(Comparator.comparingInt(t -> t.getQuestionTags().size())).limit(24).toList();
        var currentStartIndex = Math.max((startIndex - 1) * 10, 0);
        var totalPages = (int) Math.ceil((double) tags.size() / 24);
        var endIndex = Math.min(currentStartIndex + 24, tags.size());

        if (currentStartIndex >= tags.size()) {
            return new TagPageDTO(new PaginationDTO(startIndex, 1, totalPages), new HashSet<>());
        }

        var pagination = new PaginationDTO(startIndex,
                1,
                totalPages);

        return new TagPageDTO(pagination,
                sorted.subList(currentStartIndex, endIndex).stream()
                        .map(Tag::statsDto)
                        .collect(Collectors.toSet())
        );

    }

    public List<AutocompleteResult> getMatchingTags(String substring, Set<AutocompleteResult> chosenTags) {
        if (substring.isEmpty()) {
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
