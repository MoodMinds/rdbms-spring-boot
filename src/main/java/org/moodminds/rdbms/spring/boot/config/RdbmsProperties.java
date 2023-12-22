package org.moodminds.rdbms.spring.boot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * The RDBMS configuration properties bean.
 */
@ConfigurationProperties(prefix = "spring.rdbms")
public class RdbmsProperties {

    private Integer fetch;
    private Integer batch;
    private List<String> render;

    /**
     * Return the fetch size property value.
     *
     * @return the fetch size property value
     */
    public Integer getFetch() {
        return fetch;
    }

    /**
     * Set the fetch size property value.
     *
     * @param fetch the fetch size property value
     */
    public void setFetch(Integer fetch) {
        this.fetch = fetch;
    }

    /**
     * Return the batch size property value.
     *
     * @return the batch size property value
     */
    public Integer getBatch() {
        return batch;
    }

    /**
     * Set the batch size property value.
     *
     * @param batch the fetch size property value
     */
    public void setBatch(Integer batch) {
        this.batch = batch;
    }

    /**
     * Get the RDBMS render class names.
     *
     * @return the RDBMS render class names
     */
    public List<String> getRender() {
        return render;
    }

    /**
     * Set the RDBMS render class names.
     */
    public void setRender(List<String> render) {
        this.render = render;
    }
}
