package be.ugent.vopro5.backend.businesslayer.businessentities.models;

import be.ugent.vopro5.backend.businesslayer.businessentities.validators.ValidationException;

import java.util.*;

import static be.ugent.vopro5.backend.businesslayer.businessentities.validators.Validators.*;

/**
 * Created on 24/02/16.
 */
public class Travel extends Identifiable {
    private static final String NAME_CAN_NOT_BE_BLANK = "Name can not be blank";
    private static final String THIS_TRAVEL_DOES_NOT_CONTAIN = "This travel does not contain ";

    private final Address startPoint;
    private final Address endPoint;
    private String name;
    private boolean isArrivalTime;
    private Set<Route> routes;
    private Set<TimeConstraint> timeConstraints;

    /**
     * create a new travel. A travel defines a start and endpoint and can have multiple routes that each define a concrete
     * way to reach the endpoints departing from the starting point. Note that all routes of a travel share the same
     * start and endpoint. To be useful travels should contain at least one route.
     * @param identifier The UUID of the travel.
     * @param name The name given to this particular travel by the user. If a user decides to create a travel
     *             with the starting point his/her home address and the endpoint his/her work address it could be named "home-work".
     * @param isArrivalTime If true then the TimeIntervalConstraint represents the interval of arrival. ex. A user
     *                      may want to reach the endpoint between 10:00 - 10:30. If false then the TimeIntervalConstraints
     *                      represents the interval of departure. ex. A user may want to depart from the starting point
     *                      between 20:00 and 20:20. Currently isArrivalTime is nowhere used.
     * @param startPoint The address that represents the departing location of the travel.
     * @param endPoint The address that represents the destination or endpoint of the travel.
     * @param routes The concrete ways the reach the endpoint departing from the starting point.
     * @param timeConstraints The set of time constraints created by a user. ex. a user can create a WeekDayConstraint
     *                        [1,1,0,0,0,0,0] to indicate that he/she only makes this travel on monday and tuesday. Furthermore
     *                        the user can add a TimeIntervalConstraint "20:00-21:00" indicating the approximate time of traveling.
     *                        Notifications are only sent to a user if the timeConstraints are valid.
     * @throws ValidationException
     */
    public Travel(UUID identifier, String name, boolean isArrivalTime, Address startPoint, Address endPoint, Set<Route> routes, Set<TimeConstraint> timeConstraints) throws ValidationException {
        super(identifier);
        notBlank(name, NAME_CAN_NOT_BE_BLANK);
        this.name = name;
        this.isArrivalTime = isArrivalTime;
        notNull(startPoint, "StartPoint can not be null");
        this.startPoint = startPoint;
        notNull(endPoint, "EndPoint can not be null");
        this.endPoint = endPoint;
        notNull(routes, "Routes can not be null");
        this.routes = new HashSet<>(routes);
        notEmpty(timeConstraints, "TimeConstraints can not be empty");
        this.timeConstraints = timeConstraints;
    }

    /**
     * create a new travel with a single weekday constraint
     * @param name
     * @param isArrivalTime
     * @param startPoint
     * @param endPoint
     * @param timeIntervalConstraint
     * @param recurring
     * @throws ValidationException
     */
    public Travel(String name, boolean isArrivalTime, Address startPoint, Address endPoint, TimeIntervalConstraint timeIntervalConstraint, boolean[] recurring) throws ValidationException {
        this(UUID.randomUUID(), name, isArrivalTime, startPoint, endPoint, new HashSet<>(), new HashSet<>(Arrays.asList(timeIntervalConstraint, new WeekDayConstraint(recurring))));
    }

    /**
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     * @throws ValidationException
     */
    public void setName(String name) throws ValidationException {
        notBlank(name, NAME_CAN_NOT_BE_BLANK);
        this.name = name;
    }

    /**
     * @return
     */
    public boolean isArrivalTime() {
        return isArrivalTime;
    }

    /**
     * @param isArrivalTime
     */
    public void setIsArrivalTime(boolean isArrivalTime) {
        this.isArrivalTime = isArrivalTime;
    }

    /**
     * @return
     */
    public Address getStartPoint() {
        return startPoint;
    }

    /**
     * @return
     */
    public Address getEndPoint() {
        return endPoint;
    }

    /**
     * @return
     */
    public Set<Route> getRoutes() {
        return Collections.unmodifiableSet(routes);
    }

    /**
     * add a route to this travel
     * @param route
     */
    public void addRoute(Route route) {
        notNull(route,"Route cannot be null");
        routes.add(route);
    }

    /**
     * remove a route from this travel
     * @param route
     */
    public void removeRoute(Route route) {
        validationAssert(routes.size() > 0, "Cannot remove from an empty set");
        validationAssert(routes.contains(route), THIS_TRAVEL_DOES_NOT_CONTAIN + route.toString());
        routes.remove(route);
    }

    /**
     * @return
     */
    public Set<TimeConstraint> getTimeConstraints() {
        return Collections.unmodifiableSet(timeConstraints);
    }

    /**
     * add a time constraint to this route
     * @param timeConstraint
     * @throws ValidationException
     */
    public void addTimeConstraint(TimeConstraint timeConstraint) throws ValidationException {
        notNull(timeConstraint, "TimeConstraint can not be null");
        timeConstraints.add(timeConstraint);
    }

    /**
     * remove a time constraint from this route
     * @param timeConstraint
     * @throws ValidationException
     */
    public void removeTimeContstraint(TimeConstraint timeConstraint) throws ValidationException {
        validationAssert(timeConstraints.size() > 1, "Cannot remove last element of timeconstraints");
        validationAssert(timeConstraints.contains(timeConstraint), THIS_TRAVEL_DOES_NOT_CONTAIN + timeConstraint.toString());
        timeConstraints.remove(timeConstraint);
    }

    /**
     * Makes a deepcopy of properties from another travel. The properties must be modifiable.
     * @param other: the travel from which to make a deepcopy of the underlining properties.
     * @throws ValidationException
     */
    public void transferProperties(Travel other) throws ValidationException {
        setIsArrivalTime(other.isArrivalTime);
        setName(other.getName());
        this.timeConstraints = new HashSet<>(other.getTimeConstraints());
    }

    @Override
    public String toString() {
        return "Travel{" +
                "startPoint=" + startPoint +
                ", endPoint=" + endPoint +
                ", name='" + name + '\'' +
                ", isArrivalTime=" + isArrivalTime +
                ", routes=" + routes +
                ", timeConstraints=" + timeConstraints +
                '}';
    }
}
