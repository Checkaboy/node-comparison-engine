package com.checkaboy.comparator.test;

import com.checkaboy.comparator.CollectionComparator;
import com.checkaboy.comparator.ObjectComparatorBuilder;
import com.checkaboy.comparator.context.IContextualComparator;
import com.checkaboy.comparator.context.cache.ComparisonContext;
import com.checkaboy.comparator.policy.DeclarativeComparisonPolicy;
import com.checkaboy.comparator.policy.IComparisonPolicy;
import com.checkaboy.comparator.registery.DefaultComparatorRegistryBuilder;
import com.checkaboy.comparator.registery.IComparatorRegistry;
import com.checkaboy.comparator.rule.ExcludeFieldRule;
import com.checkaboy.comparator.rule.IncludeFieldRule;
import com.checkaboy.comparator.test.model.dto.AuthorDto;
import com.checkaboy.comparator.test.model.dto.BookDto;
import com.checkaboy.comparator.test.model.entity.AuthorEntity;
import com.checkaboy.comparator.test.model.entity.BookEntity;
import com.google.common.reflect.TypeToken;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Objects;

/**
 * @author Taras Shaptala
 */
public class ComparatorArchitectureTest {

    @Test
    public void shouldCompareDtoEntityCycleAndPolicyRules() {
        TypeToken<AuthorEntity> ae = TypeToken.of(AuthorEntity.class);
        TypeToken<AuthorDto> ad = TypeToken.of(AuthorDto.class);
        TypeToken<BookEntity> be = TypeToken.of(BookEntity.class);
        TypeToken<BookDto> bd = TypeToken.of(BookDto.class);

        DefaultComparatorRegistryBuilder builder = new DefaultComparatorRegistryBuilder();
        builder.register(TypeToken.of(Long.class), TypeToken.of(Long.class), c -> (ctx, s, t) -> Objects.equals(s, t));
        builder.register(TypeToken.of(String.class), TypeToken.of(String.class), c -> (ctx, s, t) -> Objects.equals(s, t));

        new ObjectComparatorBuilder<>(ae, ad, builder)
                .field("id", AuthorEntity::getId, AuthorDto::getId, (ctx, s, t) -> Objects.equals(s, t))
                .field("firstName", TypeToken.of(String.class), TypeToken.of(String.class), AuthorEntity::getFirstName, AuthorDto::getFirstName)
                .field("lastName", TypeToken.of(String.class), TypeToken.of(String.class), AuthorEntity::getLastName, AuthorDto::getLastName)
                .field("books", AuthorEntity::getBooks, AuthorDto::getBooks, new CollectionComparator<>(be, bd))
                .buildAndRegister();

        new ObjectComparatorBuilder<>(be, bd, builder)
                .field("id", BookEntity::getId, BookDto::getId, (ctx, s, t) -> Objects.equals(s, t))
                .field("name", TypeToken.of(String.class), TypeToken.of(String.class), BookEntity::getName, BookDto::getName)
                .field("author", ae, ad, BookEntity::getAuthor, BookDto::getAuthor)
                .buildAndRegister();

        IComparatorRegistry registry = builder.build();

        AuthorEntity authorEntity = new AuthorEntity(1L, "John", "Steinbeck");
        BookEntity bookEntity = new BookEntity(10L, "East of Eden");
        authorEntity.setBooks(List.of(bookEntity));
        bookEntity.setAuthor(authorEntity);

        AuthorDto authorDto = new AuthorDto(1L, "John", "Steinbeck");
        BookDto bookDto = new BookDto(10L, "East of Eden");
        authorDto.setBooks(List.of(bookDto));
        bookDto.setAuthor(authorDto);

        IContextualComparator<AuthorEntity, AuthorDto> comparator = registry.resolve(ae, ad, new ComparisonContext(registry, new DeclarativeComparisonPolicy(List.of())));

        IComparisonPolicy full = new DeclarativeComparisonPolicy(List.of());
        Assert.assertTrue(comparator.compare(new ComparisonContext(registry, full), authorEntity, authorDto));

        authorDto.setFirstName("Jane");
        IComparisonPolicy idOnly = new DeclarativeComparisonPolicy(List.of(new IncludeFieldRule("id")));
        Assert.assertTrue(comparator.compare(new ComparisonContext(registry, idOnly), authorEntity, authorDto));

        IComparisonPolicy excludeBooks = new DeclarativeComparisonPolicy(List.of(new ExcludeFieldRule("books")));
        bookDto.setName("Different");
        Assert.assertFalse(comparator.compare(new ComparisonContext(registry, excludeBooks), authorEntity, authorDto));
    }

    @Test
    public void shouldResolveByAssignableTypesAndKeepRegistryImmutableAfterBuild() {
        DefaultComparatorRegistryBuilder builder = new DefaultComparatorRegistryBuilder();
        builder.register(TypeToken.of(Number.class), TypeToken.of(Number.class), c -> (ctx, s, t) -> s.longValue() == t.longValue());
        IComparatorRegistry registry = builder.build();
        IContextualComparator<Integer, Integer> comparator = registry.resolve(TypeToken.of(Integer.class), TypeToken.of(Integer.class), new ComparisonContext(registry, new DeclarativeComparisonPolicy(List.of())));
        Assert.assertTrue(comparator.compare(new ComparisonContext(registry, new DeclarativeComparisonPolicy(List.of())), 5, 5));

        builder.register(TypeToken.of(Integer.class), TypeToken.of(Integer.class), c -> (ctx, s, t) -> false);
        Assert.assertTrue(comparator.compare(new ComparisonContext(registry, new DeclarativeComparisonPolicy(List.of())), 8, 8));
    }

}
