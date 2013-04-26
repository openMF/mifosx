package org.mifosplatform.paginationutil;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Class PaginationHelper is a utility class where sql queries are sent with limit and offset parameters along with them.
 * @param <E>
 */
public class PaginationHelper<E> {

    /**
     * method fetchPage performs the query over the database,applies pagination over the result set and returns a Page object.
     * @param jt - JdbcTemplate object
     * @param sqlCountRows            - sql query for count(*) method to count total rows
     * @param sqlFetchRows            - actual sql query for fetching all the rows
     * @param args                    - args for the sqlFetchRows query
     * @param limit                   - limit parameter
     * @param offset                  - offset parameter
     * @param rowMapper               - rowMapper for mapping rows of result set
     * @return
     */
        public Page<E> fetchPage(final JdbcTemplate jt, final String sqlCountRows, final String sqlFetchRows, final Object args[],
                             final int limit, final int offset, final RowMapper<E> rowMapper) {

        // determine how many rows are available
        final int rowCount = jt.queryForInt(sqlCountRows, args);

        final int pageNo = (int)(offset/limit) + 1;
        final int pageSize = limit;

        // calculate the number of pages
        int pageCount = rowCount / limit;
        if (rowCount > limit * pageCount) {
            pageCount++;
        }

        // create the page object
        final Page<E> page = new Page<E>();
        //page.setPageNumber(pageNo);
        //page.setPagesAvailable(pageCount);


        // fetch a single page of results
        final int startRow = (pageNo - 1) * pageSize;

        // performing the sql query and using ResultSetExtactor to extract the required set of results to form a Page object and return.
        jt.query(sqlFetchRows, new ResultSetExtractor<E>() {
            @Override
            public E extractData(ResultSet rs) throws SQLException, DataAccessException {
                final List<E> pageItems = page.getPageItems();
                int currentRow = 0;
                while (rs.next() && currentRow < startRow + pageSize) {
                    if (currentRow >= startRow) {
                        pageItems.add(rowMapper.mapRow(rs, currentRow));
                    }
                    currentRow++;
                }
                return (E) page;
            }
        });

        return page;
    }

}