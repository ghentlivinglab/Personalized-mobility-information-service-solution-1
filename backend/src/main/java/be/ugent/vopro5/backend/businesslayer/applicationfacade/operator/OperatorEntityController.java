package be.ugent.vopro5.backend.businesslayer.applicationfacade.operator;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.Operator;
import be.ugent.vopro5.backend.businesslayer.businessentities.validators.ValidationException;
import be.ugent.vopro5.backend.businesslayer.util.APIException;
import be.ugent.vopro5.backend.businesslayer.util.ControllerCheck;
import be.ugent.vopro5.backend.businesslayer.util.constants.ControllerConstants;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.DataAccessProvider;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.OperatorDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;

/**
 * Created by thibault on 4/4/16.
 */
@RestController
@RequestMapping("/operator/{id}/")
public class OperatorEntityController {

    @Autowired
    private DataAccessProvider dataAccessProvider;


    /**
     * Get details about a specific operator
     *
     * @param id The ID of the operator we want to get.
     * @return The requested operator.
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET)
    public Operator getOperator(@PathVariable String id) {
        Operator operator = dataAccessProvider.getDataAccessContext().getOperatorDAO().find(id);
        ControllerCheck.notNull(operator, Operator.class);
        return operator;
    }

    /**
     * Delete an operator
     *
     * @param id The ID of the operator
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = RequestMethod.DELETE)
    public void deleteOperator(@PathVariable String id) {
        OperatorDAO operatorDAO = dataAccessProvider.getDataAccessContext().getOperatorDAO();
        Operator operator = operatorDAO.find(id);
        ControllerCheck.notNull(operator, Operator.class);
        operatorDAO.delete(operator);
    }

    /**
     * Edit an operator
     *
     * @param id       The ID of the operator we want to update.
     * @param operator Contains the new properties of the operator
     * @return The updated operator.
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.PUT)
    public Operator updateOperator(@PathVariable String id, @Valid @RequestBody Operator operator) throws ValidationException {
        OperatorDAO operatorDAO = dataAccessProvider.getDataAccessContext().getOperatorDAO();
        Operator prev = operatorDAO.find(id);
        ControllerCheck.notNull(prev, Operator.class);

        Operator sameEmail = operatorDAO.findByEmail(operator.getEmail());
        if (sameEmail != null && !sameEmail.getIdentifier().equals(prev.getIdentifier())) {
            throw new APIException.DataConflictException(ControllerConstants.EMAIL_ALREADY_IN_USE, Collections.singletonList("email"));
        }

        prev.transferProperties(operator);
        operatorDAO.update(prev);
        return prev;
    }

}
