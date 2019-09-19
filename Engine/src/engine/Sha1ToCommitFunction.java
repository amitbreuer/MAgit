package engine;

import puk.team.course.magit.ancestor.finder.CommitRepresentative;

import java.io.IOException;
import java.util.function.Function;

public class Sha1ToCommitFunction implements Function {
    private MagitManager magitManager;

    public Sha1ToCommitFunction(MagitManager magitManager) {
        this.magitManager = magitManager;
    }

    @Override
    public CommitRepresentative apply(Object sha1) {
        CommitRepresentative commitRepresentative = null;
        try {
            commitRepresentative = magitManager.CreateCommitFromSha1(((String)sha1),magitManager.GetObjectsDirPath());
        } catch (Exception e) {
        }
        return commitRepresentative;
    }
}
