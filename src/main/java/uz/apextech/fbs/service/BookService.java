package uz.apextech.fbs.service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.apextech.fbs.domain.Book;
import uz.apextech.fbs.repository.BookRepository;
import uz.apextech.fbs.service.dto.BookDTO;
import uz.apextech.fbs.service.mapper.BookMapper;

/**
 * Service Implementation for managing {@link Book}.
 */
@Service
@Transactional
public class BookService {

    private final Logger log = LoggerFactory.getLogger(BookService.class);

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    public BookService(BookRepository bookRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    /**
     * Save a book.
     *
     * @param bookDTO the entity to save.
     * @return the persisted entity.
     */
    public BookDTO save(BookDTO bookDTO) {
        log.debug("Request to save Book : {}", bookDTO);
        Book book = bookMapper.toEntity(bookDTO);
        book = bookRepository.save(book);
        return bookMapper.toDto(book);
    }

    /**
     * Partially update a book.
     *
     * @param bookDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<BookDTO> partialUpdate(BookDTO bookDTO) {
        log.debug("Request to partially update Book : {}", bookDTO);

        return bookRepository
            .findById(bookDTO.getId())
            .map(existingBook -> {
                bookMapper.partialUpdate(existingBook, bookDTO);

                return existingBook;
            })
            .map(bookRepository::save)
            .map(bookMapper::toDto);
    }

    /**
     * Get all the books.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<BookDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Books");
        return bookRepository.findAll(pageable).map(bookMapper::toDto);
    }

    /**
     * Get all the books with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<BookDTO> findAllWithEagerRelationships(Pageable pageable) {
        return bookRepository.findAllWithEagerRelationships(pageable).map(bookMapper::toDto);
    }

    /**
     * Get one book by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BookDTO> findOne(Long id) {
        log.debug("Request to get Book : {}", id);
        return bookRepository.findOneWithEagerRelationships(id).map(bookMapper::toDto);
    }

    /**
     * Delete the book by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Book : {}", id);
        bookRepository.deleteById(id);
    }
}
