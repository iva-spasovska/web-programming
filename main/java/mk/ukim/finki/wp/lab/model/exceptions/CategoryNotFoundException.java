package mk.ukim.finki.wp.lab.model.exceptions;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(Long id) {
        super(String.format("Category with id '%d' was not found.", id));
    }
}
