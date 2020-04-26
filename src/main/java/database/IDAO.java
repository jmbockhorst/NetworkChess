package database;

import java.util.List;

public interface IDAO<T> {
    public List<T> list();

    public void insert(T item);

    public void update(T item);
}