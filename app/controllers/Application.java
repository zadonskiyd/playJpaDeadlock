package controllers;

import play.libs.F;
import play.mvc.*;
import play.data.*;
import static play.data.Form.*;
import play.db.jpa.*;

import views.html.*;

import models.*;

/**
 * Manage a database of computers
 */
public class Application extends Controller {
    
    /**
     * This result directly redirect to application home.
     */
    public static Result GO_HOME = redirect(
        routes.Application.list(0, "name", "asc", "")
    );
    
    /**
     * Handle default path requests, redirect to computers list
     */
    public static Result index() {
        return GO_HOME;
    }

    /**
     * Display the paginated list of computers.
     *
     * @param page Current page number (starts from 0)
     * @param sortBy Column to be sorted
     * @param order Sort order (either asc or desc)
     * @param filter Filter applied on computer names
     */
   // @Transactional(readOnly=false)
    public static Result list(int page, String sortBy, String order, String filter) {
        final int fPage = page;
        final String fSortBy = sortBy;
        final String fOrder = order;
        final String fFilter = filter;
        try {
            Content content = JPA.withTransaction(new F.Function0<Content>() {
                @Override
                public Content apply() throws Throwable {
                    return list.render(
                            Computer.page(fPage, 10, fSortBy, fOrder, fFilter),
                            fSortBy, fOrder, fFilter
                    );
                }
            });
            return ok(content);
        } catch (Throwable th) {
            th.printStackTrace();
            return badRequest();
        }
    }
    
    /**
     * Display the 'edit form' of a existing Computer.
     *
     * @param id Id of the computer to edit
     */
    @Transactional(readOnly=true)
    public static Result edit(Long id) {
        Form<Computer> computerForm = form(Computer.class).fill(
            Computer.findById(id)
        );
        return ok(
            editForm.render(id, computerForm)
        );
    }
    
    /**
     * Handle the 'edit form' submission 
     *
     * @param id Id of the computer to edit
     */
    @Transactional
    public static Result update(Long id) {
        Form<Computer> computerForm = form(Computer.class).bindFromRequest();
        if(computerForm.hasErrors()) {
            return badRequest(editForm.render(id, computerForm));
        }
        computerForm.get().update(id);
        flash("success", "Computer " + computerForm.get().name + " has been updated");
        return GO_HOME;
    }
    
    /**
     * Display the 'new computer form'.
     */
    @Transactional(readOnly=true)
    public static Result create() {
        Form<Computer> computerForm = form(Computer.class);
        return ok(
            createForm.render(computerForm)
        );
    }
    
    /**
     * Handle the 'new computer form' submission 
     */
    @Transactional
    public static Result save() {
        Form<Computer> computerForm = form(Computer.class).bindFromRequest();
        if(computerForm.hasErrors()) {
            return badRequest(createForm.render(computerForm));
        }
        computerForm.get().save();
        flash("success", "Computer " + computerForm.get().name + " has been created");
        return GO_HOME;
    }
    
    /**
     * Handle computer deletion
     */
    @Transactional
    public static Result delete(Long id) {
        Computer.findById(id).delete();
        flash("success", "Computer has been deleted");
        return GO_HOME;
    }
    

}
            
