package edu.csula.rubrics.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

//@Document(indexName = "criteria")
//@Setting(settingPath = "es-config/elastic-analyzer.json")
@Entity
@Table(name = "criteria")
public class Criterion implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

//	@Field(type = FieldType.Text, analyzer = "autocomplete_index", searchAnalyzer = "autocomplete_search")
	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String description;

	private boolean deleted;

	/* Each criterion has a number of ratings. */
	@OneToMany(mappedBy = "criterion")
	@OrderBy("value desc")
	private List<Rating> ratings;

	@ManyToMany
	@JoinTable(name = "criterion_tags", joinColumns = @JoinColumn(name = "criterion_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
	private List<Tag> tags;

	@Column(name = "publish_date")
	private Calendar publishDate;

	public Criterion() {
		ratings = new ArrayList<Rating>();
		tags = new ArrayList<Tag>();
	}

	public Criterion clone() {
		Criterion newCriterion = new Criterion();
		newCriterion.description = description;
		for (Rating rating : ratings)
			newCriterion.ratings.add(rating.clone());

		return newCriterion;
	}

	public boolean isPublished() {
		return publishDate != null && Calendar.getInstance().after(publishDate);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public List<Rating> getRatings() {
		return ratings;
	}

	public void setRatings(List<Rating> ratings) {
		this.ratings = ratings;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public Calendar getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(Calendar publishDate) {
		this.publishDate = publishDate;
	}

}
