package be.ugent.vopro5.backend.businesslayer.applicationfacade.operator;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.Operator;
import be.ugent.vopro5.backend.businesslayer.util.APIException;
import be.ugent.vopro5.backend.businesslayer.util.constants.ControllerConstants;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.DataAccessProvider;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.OperatorDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

/**
 * Created by thibault on 4/4/16.
 */
@RestController
@RequestMapping("/operator/")
public class OperatorIndexController {

    @Autowired
    private DataAccessProvider dataAccessProvider;

    /**
     * Create a new Operator
     *
     * @param operator Contains the properties of the operator that we want to create.
     * @return The created user.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public Operator createOperator(@Valid @RequestBody Operator operator) {
        OperatorDAO operatorDAO = dataAccessProvider.getDataAccessContext().getOperatorDAO();

        if (operatorDAO.findByEmail(operator.getEmail()) != null) {
            throw new APIException.DataConflictException(ControllerConstants.EMAIL_ALREADY_IN_USE, Collections.singletonList("email"));
        }

        if (operator.getPassword() == null || "".equals(operator.getPassword().trim())) {
            throw new APIException.BadDataException("You must provide a password", Collections.singletonList("password"));
        }

        operatorDAO.insert(operator);
        return operator;
    }

    /**
     * List all operators
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET)
    public List<Operator> getOperators() {
        return dataAccessProvider.getDataAccessContext().getOperatorDAO().listAll();
    }
}
