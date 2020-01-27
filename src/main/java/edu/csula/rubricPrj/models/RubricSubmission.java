package edu.csula.rubricPrj.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;


/**
 * RubricSubmission is the collection of all the rubric evaluations of a
 * student.
 */
@Entity
@Table(name = "rubric_submissions")
public class RubricSubmission implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne
    @JoinColumn(name = "assignment_id", nullable = false)
    private RubricAssignment assignment;

    @OneToMany(mappedBy = "submission",
        cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    private List<RubricEvaluation> evaluations;

    // Although the following counts can be calculated from evaluations, it's
    // better to create separate fields for them for performance reasons. In
    // particular, the submission list page can be generated much more
    // efficiently without instantiating the evaluation collection in each
    // RubricSubmissoin.

    @Column(name = "instructor_evaluation_count", nullable = false)
    private int instructorEvaluationCount;

    @Column(name = "peer_evaluation_count", nullable = false)
    private int peerEvaluationCount;

    @Column(name = "external_evaluation_count", nullable = false)
    private int externalEvaluationCount;

    @Transient
    private Map<String, int[]> ratingsByType;

    public RubricSubmission()
    {
        evaluations = new ArrayList<RubricEvaluation>();
        instructorEvaluationCount = 0;
        externalEvaluationCount = 0;
        peerEvaluationCount = 0;
    }

    public RubricSubmission( User student, RubricAssignment assignment )
    {
        this();
        this.student = student;
        this.assignment = assignment;
    }

    public RubricEvaluation getEvaluation( User user )
    {
        for( RubricEvaluation evaluation : evaluations )
            if( evaluation.getEvaluator().getId().equals( user.getId() ) )
                return evaluation;
        return null;
    }

    public List<RubricEvaluation> getInstructorEvaluations()
    {
        List<RubricEvaluation> instructorEvaluations = new ArrayList<RubricEvaluation>();
        for( RubricEvaluation evaluation : evaluations )
            if( evaluation.getType() == RubricEvaluation.Type.INSTRUCTOR )
                instructorEvaluations.add( evaluation );
        return instructorEvaluations;
    }

    public List<RubricEvaluation> getPeerEvaluations()
    {
        List<RubricEvaluation> peerEvaluations = new ArrayList<RubricEvaluation>();
        for( RubricEvaluation evaluation : evaluations )
            if( evaluation.getType() == RubricEvaluation.Type.PEER )
                peerEvaluations.add( evaluation );
        return peerEvaluations;
    }

    public List<RubricEvaluation> getExternalEvaluations()
    {
        List<RubricEvaluation> externalEvaluations = new ArrayList<RubricEvaluation>();
        for( RubricEvaluation evaluation : evaluations )
            if( evaluation.getType() == RubricEvaluation.Type.EXTERNAL )
                externalEvaluations.add( evaluation );
        return externalEvaluations;
    }

    public int getTotalEvaluationCount()
    {
        return instructorEvaluationCount + externalEvaluationCount
            + peerEvaluationCount;
    }

    public int incrementInstructorEvaluationCount()
    {
        return ++instructorEvaluationCount;
    }

    public int incrementPeerEvaluationCount()
    {
        return ++peerEvaluationCount;
    }

    public int incrementExternalEvaluationCount()
    {
        return ++externalEvaluationCount;
    }

    /* Aggregate the ratings for each evaluation type */
    public void aggregateRatings()
    {
        Map<String, List<RubricEvaluation>> evaluationsByType = new HashMap<String, List<RubricEvaluation>>();
        for( RubricEvaluation evaluation : evaluations )
        {
            if( !evaluation.isCompleted() ) continue;

            List<RubricEvaluation> evals = evaluationsByType
                .get( evaluation.getType().name() );
            if( evals == null )
            {
                evals = new ArrayList<RubricEvaluation>();
                evaluationsByType.put( evaluation.getType().name(), evals );
            }
            evals.add( evaluation );
        }

        ratingsByType = new HashMap<String, int[]>();
        for( String key : evaluationsByType.keySet() )
        {
            int[] ratings = new int[assignment.getRubric()
                .getCriterion()
                .size()];

            List<RubricEvaluation> evals = evaluationsByType.get( key );
            for( RubricEvaluation eval : evals )
                for( int i = 0; i < ratings.length; ++i )
                    ratings[i] += eval.getRatings().get( i );
            for( int i = 0; i < ratings.length; ++i )
                ratings[i] = (int) (Math
                    .round( ratings[i] * 1.0 / evals.size() ));

            ratingsByType.put( key, ratings );
        }
    }

    public Long getId()
    {
        return id;
    }

    public void setId( Long id )
    {
        this.id = id;
    }

    public User getStudent()
    {
        return student;
    }

    public void setStudent( User student )
    {
        this.student = student;
    }

    public RubricAssignment getAssignment()
    {
        return assignment;
    }

    public void setAssignment( RubricAssignment assignment )
    {
        this.assignment = assignment;
    }

    public List<RubricEvaluation> getEvaluations()
    {
        return evaluations;
    }

    public void setEvaluations( List<RubricEvaluation> evaluations )
    {
        this.evaluations = evaluations;
    }

    public int getInstructorEvaluationCount()
    {
        return instructorEvaluationCount;
    }

    public void setInstructorEvaluationCount( int instructorEvaluationCount )
    {
        this.instructorEvaluationCount = instructorEvaluationCount;
    }

    public int getPeerEvaluationCount()
    {
        return peerEvaluationCount;
    }

    public void setPeerEvaluationCount( int peerEvaluationCount )
    {
        this.peerEvaluationCount = peerEvaluationCount;
    }

    public int getExternalEvaluationCount()
    {
        return externalEvaluationCount;
    }

    public void setExternalEvaluationCount( int externalEvaluationCount )
    {
        this.externalEvaluationCount = externalEvaluationCount;
    }

    public Map<String, int[]> getRatingsByType()
    {
        return ratingsByType;
    }

    public void setRatingsByType( Map<String, int[]> ratingsByType )
    {
        this.ratingsByType = ratingsByType;
    }

}
