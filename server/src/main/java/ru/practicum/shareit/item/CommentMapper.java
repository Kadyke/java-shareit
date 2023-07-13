package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.CommentDto;

import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

@UtilityClass
public class CommentMapper {

    public Comment toComment(CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setId(commentDto.getId());
        comment.setText(commentDto.getText());
        if (commentDto.getItem() == null) {
            comment.setItem(null);
        } else {
            comment.setItem(ItemMapper.toItem(commentDto.getItem()));
        }
        comment.setCreatedTime(commentDto.getCreated());
        return comment;
    }

    public CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setItem(ItemMapper.toItemDto(comment.getItem()));
        commentDto.setAuthorName(comment.getAuthor().getName());
        commentDto.setCreated(comment.getCreatedTime());
        return commentDto;
    }

    public List<CommentDto> collectionToCommentDto(Collection<Comment> comments) {
        return comments.stream().map(CommentMapper::toCommentDto).collect(toList());
    }
}
